package cfh.taxi;

import static cfh.taxi.Program.InputOutput.Level.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;
import cfh.taxi.node.NodeType;

public abstract class Location extends Node {
    
    protected final Queue waiting = createQueue();

    protected Location(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
        if (name.isEmpty()) throw new IllegalArgumentException("empty name");
     }
    
    public boolean isEmpty() {
        return waiting.isEmpty();
    }
    
    @Override
    public void reset() {
        waiting.clear();
        super.reset();
    }
    
    public Passenger addWaiting(Value value) throws TaxiException {
        if (value == null) throw new IllegalArgumentException("null value");
        
        throw new TaxiException("passengers cannot be made to wait at " + this);
    }

    public Passenger pickupPassenger(InputOutput inpout) throws TaxiException {
        if (waiting.isEmpty())
            throw new TaxiException("no passenger waiting at " + this);
        return waiting.pop();
    }
    
    protected boolean hasCapacity() {
        return true;
    }
    
    public void addOutgoing(Passenger passenger) throws TaxiException {
        if (passenger == null) throw new IllegalArgumentException("null passenger");
        
        waiting.push(passenger);
    }
    
    public final Result arrival(Taxi taxi, Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (taxi == null) throw new IllegalArgumentException("null taxi");
        if (incoming == null) throw new IllegalArgumentException("null incomming");
        
        if (inpout.isLogging(INFO)) {
            StringBuilder builder = new StringBuilder("waiting:");
            for (Passenger passenger : waiting.queue) {
                builder.append(" ").append(passenger.value.toString());
            }
            inpout.log(null, builder.toString());
            
            if (!incoming.isEmpty()) {
                builder.setLength(0);
                builder.append("dropping:");
                for (Passenger passenger : incoming) {
                    builder.append(" ").append(passenger.value.toString());
                }
                inpout.log(null, builder.toString());
            }
        }
        
        payment(taxi, incoming);
        receive(incoming, inpout);
        return arrived(taxi);
    }
    
    @Override
    public List<Passenger> waiting() {
        return Collections.unmodifiableList(waiting.queue);
    }
    
    @Override
    public int waitingCount() {
        return waiting.size();
    }
    
    protected void payment(Taxi taxi, Deque<Passenger> incoming) {
        if (taxi == null) throw new IllegalArgumentException("null taxi");
        if (incoming == null) throw new IllegalArgumentException("null incomming");

        for (Passenger passenger : incoming) {
            passenger.payFare(taxi);
        }
    }
    
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming == null) throw new IllegalArgumentException("null incomming");
        
        while (hasCapacity() && !incoming.isEmpty()) {
            addOutgoing(incoming.removeFirst());
        }
    }
    
    protected Result arrived(Taxi taxi) {
        if (taxi == null) throw new IllegalArgumentException("null taxi");
        
        return null;
    }
    
    protected Queue createQueue() {
        return new FIFOQueue();
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected static abstract class Queue {
        
        protected final List<Passenger> queue = new ArrayList<>();
        
        public abstract void push(Passenger passenger);
        
        public void pushAll(List<Passenger> outgoing) {
            if (outgoing == null) throw new IllegalArgumentException("null outgoing");
            
            for (Passenger passenger : outgoing) {
                push(passenger);
            }
        }

        public Passenger pop() {
            return queue.remove(0);
        }
        
        public void clear() {
            queue.clear();
        }
        
        public boolean isEmpty() {
            return queue.isEmpty();
        }
        
        public int size() {
            return queue.size();
        }
    }
    
    public static class FIFOQueue extends Queue {
        
        @Override
        public void push(Passenger passenger) {
            if (passenger == null) throw new IllegalArgumentException("null passenger");
            
            queue.add(passenger);
        }
     }
    
    public static class LIFOQueue extends Queue {
        
        @Override
        public void push(Passenger passenger) {
            if (passenger == null) throw new IllegalArgumentException("null passenger");
            
            queue.add(0, passenger);
        }
    }
    
    public static class RANDOMQueue extends Queue {
        
        private final Random random = new Random();
        
        @Override
        public void push(Passenger passenger) {
            if (passenger == null) throw new IllegalArgumentException("null passenger");
            
            int i = random.nextInt(queue.size() + 1);
            queue.add(i, passenger);
        }
    }
}
