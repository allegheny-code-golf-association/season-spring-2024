package cfh.taxi.cmd;

import static cfh.taxi.Program.InputOutput.Level.*;

import cfh.taxi.Command;
import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.RoadMap;
import cfh.taxi.TaxiException;
import cfh.taxi.Taxi;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

// Pickup a[nother] passenger going to [the] Post Office.

public class Pickup extends Command {

    private final Location destination;
    
    public Pickup(int line, RoadMap map, Location destination) {
        super(line, map);
        if (destination == null) throw new IllegalArgumentException("null destination");
        
        this.destination = destination;
    }
    
    @Override
    public Result execute(Taxi taxi, InputOutput inpout) throws TaxiException {
        Passenger passenger = taxi.location().pickupPassenger(inpout);
        if (passenger != null) {
            passenger.setDestination(destination);
            taxi.addPassenger(passenger);
            inpout.log(INFO, "pickup: %s going to %s", passenger, destination);
            return null;
        } else {
            throw new TaxiException("no passenger to pick up at " + taxi.location());
        }
    }

    @Override
    public String toString() {
        return "Pickup a passenger going to " + destination + ".";
    }
}
