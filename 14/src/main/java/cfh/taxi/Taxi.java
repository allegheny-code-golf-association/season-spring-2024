package cfh.taxi;

import static cfh.taxi.Program.InputOutput.Level.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cfh.taxi.Path.Instruction;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

public class Taxi {

    public static final String VERSION = "v0.5 (beta 2018-08-22)";
    
    private final RoadMap map;
    private final InputOutput inpout;
    
    private Location location;
    private final List<Passenger> passengers = new ArrayList<>();
    
    private double tank;      // gas
    private long cash;        // cents
    private double distance;  // unit 
    private long paxCount;

    private final List<Listener> listeners = new ArrayList<>();
    
    Taxi(RoadMap map, InputOutput inpout) {
        if (map == null) throw new IllegalArgumentException("null map");
        if (inpout == null) throw new IllegalArgumentException("null inpout");
        
        this.map = map;
        this.inpout = inpout;
        
        location = map.start();
        tank = map.capacity();
        cash = 0;
        distance = 0;
        paxCount = 0;
        inpout.log(DEBUG, "taxi starts at " + location);
    }
    
    public Location location() {
        return location;
    }
    
    public double distance() {
        return distance;
    }
    
    public double tank() {
        return tank;
    }
    
    public long cash() {
        return cash;
    }
    
    public long paxCount() {
        return paxCount;
    }
    
    public List<Passenger> passengers() {
        return Collections.unmodifiableList(passengers);
    }

    public Result travelTo(Location destination, Path path) throws TaxiException {
        inpout.log(PRINT, "Driving to %s", destination);
        inpout.log(DEBUG, "  path %s", path);
        Direction direction = path.start();
        Node curr = location;
        Location last = location;
        for (Instruction instruction : path.instructions()) {
            if (Thread.currentThread().isInterrupted())
                return null;
            inpout.log(DEBUG, "  taxi driving %s to turn %s", direction, instruction);
            int count = instruction.count();
            while (count > 0) {
                if (Thread.currentThread().isInterrupted())
                    return null;
                for (Listener listener : listeners) {
                    listener.drive(this, direction);
                }
                if (curr instanceof Location) {
                    last = (Location) curr;
                }
                Node next = curr.node(direction);
                if (next == null) {
                    for (Listener listener : listeners) {
                        listener.deadEnd(this, direction);
                    }
                    throw new TaxiException("end of street " + direction + " of " + curr + " comming from " + last);
                }
                if (!move(curr, next))
                    throw new TaxiException("out of gas " + direction + " from " + curr + " to " + next);
                inpout.log(DEBUG, "  taxi at %s", next);

                if (next.isCorner()) {
                    for (Direction dir : Direction.values()) {
                        Node n = next.node(dir);
                        if (n != null && n != curr) {
                            direction = dir;
                            inpout.log(DEBUG, "  taxi turning to %s at corner", direction);
                            break;
                        }
                    }
                } else {
                    // can turn?
                    Direction dir = instruction.isTurnLeft() ? next.getLeft(curr) : next.getRight(curr);
                    if (next.node(dir) != null) {
                        count -= 1;
                        if (count == 0) {
                            direction = dir;
                            inpout.log(DEBUG, "  taxi turning %s to %s", instruction.turn(), direction);
                        }
                    }
                }
                curr = next;
            }
        }
        
        while (curr != destination) {
            if (Thread.currentThread().isInterrupted())
                return null;
            for (Listener listener : listeners) {
                listener.drive(this, direction);
            }
            Node next = curr.node(direction);
            if (next == null)
                throw new TaxiException("end of street " + direction + " of " + curr + " comming from " + last);
            if (!move(curr, next))
                throw new TaxiException("out of gas " + direction + " from " + curr + " to " + next);
            inpout.log(DEBUG, "  taxi at %s", next);
            
            if (next.isCorner()) {
                for (Direction dir : Direction.values()) {
                    Node n = next.node(dir);
                    if (n != null && n != curr) {
                        direction = dir;
                        inpout.log(DEBUG, "  taxi turning to %s at corner", direction);
                    }
                }
            }
            curr = next;
        }
        
        if (curr != destination) {
            for (Listener listener : listeners) {
                listener.lost(this, destination);
            }
            throw new TaxiException("could not reach " + destination + " on the given path");
        }
        location = destination;
        inpout.log(DEBUG, "  taxi arrived at %s", destination);
        
        Deque<Passenger> leaving = getLeaving(location);
        List<Passenger> left = new ArrayList<Passenger>(leaving);
        int index = passengers.size();
        Result result = location.arrival(this, leaving, inpout);
        if (!leaving.isEmpty()) {
            left.removeAll(leaving);
            passengers.addAll(index, leaving);
            inpout.log(DEBUG, "  %d passengers not handled", leaving.size());
            for (Passenger passenger : leaving) {
                inpout.log(DEBUG, "    %s", passenger);
            }
        }
        for (Listener listener : listeners) {
            listener.left(this, left);
        }
        return result;
    }

    public void addPassenger(Passenger passenger) throws TaxiException {
        if (passenger == null) throw new IllegalArgumentException("null passenger");
        
        if (passengers.size() >= map.maxPassengers()) {
            for (Passenger p : passengers) {
                inpout.log(DEBUG, "  %s is on taxi", p);
            }
            throw new TaxiException("cannot pickup more passengers at " + location);
        }
        passengers.add(passenger);
        passenger.resetDistance();
        for (Listener listener : listeners) {
            listener.borded(this, passenger);
        }
        inpout.log(DEBUG, "  %s entered the taxi", passenger);
    }
    
    public void receiveFare(Passenger passenger, double dist) {
        paxCount += 1;
        long amount = Math.round(dist * map.fare() * 100);
        cash += amount;
        for (Listener listener : listeners) {
            listener.cash(this, cash);
        }
        inpout.log(DEBUG, String.format("  received %.2f from %s, total cash %.2f", amount/100.0, passenger, cash/100.0));
    }

    public void refuel(int price) {
        double quant = Math.min(map.capacity()-tank, (double)cash / price);
        if (quant > 0) {
            long charge = Math.round(quant * price);
            tank += quant;
            cash -= charge;
            for (Listener listener : listeners) {
                listener.refuel(this, quant);
            }
            inpout.log(DEBUG, "  refueled %.1f gas for %.2f credits", quant, charge/100.0);
            inpout.log(INFO, "you now have %.1f gas and %.2f credits", tank, cash/100.0);
        }
    }
    
    private Deque<Passenger> getLeaving(Location destination) {
        Deque<Passenger> leaving = new LinkedList<>();
        for (Iterator<Passenger> iter = passengers.iterator(); iter.hasNext();) {
            Passenger passenger = iter.next();
            if (passenger.destination() == destination) {
                leaving.addLast(passenger);
                iter.remove();
            }
        }
        return leaving;
    }
    
    private boolean move(Node from, Node to) {
        double dist = map.distance(from, to);
        distance += dist;
        double gas = dist / map.gasUsage();
        tank -= gas;
        inpout.log(DEBUG, "  drove %.1f units, used %.2f gas, left %.1f gas", dist, gas, tank);
        for (Listener listener : listeners) {
            listener.move(this, from, to);
        }
        if (tank < 0) {
            for (Listener listener : listeners) {
                listener.outOfFuel(this);
            }
            return false;
        }
        
        for (Passenger passenger : passengers) {
            passenger.distance(dist);
        }
        return true;
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static interface Listener {

        void drive(Taxi taxi, Direction direction);

        void move(Taxi taxi, Node from, Node to);
        
        void refuel(Taxi taxi, double quant);
        
        void borded(Taxi taxi, Passenger passenger);
        
        void left(Taxi taxi, List<Passenger> left);
        
        void cash(Taxi taxi, long cash);
        
        void deadEnd(Taxi taxi, Direction direction);

        void lost(Taxi taxi, Location destination);
        
        void outOfFuel(Taxi taxi);
    }
}
