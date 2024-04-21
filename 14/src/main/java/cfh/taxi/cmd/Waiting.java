package cfh.taxi.cmd;

import static cfh.taxi.Program.InputOutput.Level.*;

import cfh.taxi.Command;
import cfh.taxi.Location;
import cfh.taxi.RoadMap;
import cfh.taxi.TaxiException;
import cfh.taxi.Taxi;
import cfh.taxi.Value;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

// "Hello, World!" is waiting at [the] Writer's Depot. 

public class Waiting extends Command {

    private final Value value;
    private final Location location;
    
    public Waiting(int line, RoadMap map, Value passenger, Location location) {
        super(line, map);
        if (passenger == null) throw new IllegalArgumentException("null passneger");
        if (location == null) throw new IllegalArgumentException("null location");
        
        this.value = passenger;
        this.location = location;
    }
    
    @Override
    public Result execute(Taxi taxi, InputOutput inpout) throws TaxiException {
        location.addWaiting(value);
        inpout.log(PRINT, "%s is waiting at %s", value, location);
        return null;
    }

    @Override
    public String toString() {
        return value + " is waiting at " + location + ".";
    }
}
