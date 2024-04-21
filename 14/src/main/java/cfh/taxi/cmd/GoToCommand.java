package cfh.taxi.cmd;

import cfh.taxi.Command;
import cfh.taxi.Location;
import cfh.taxi.RoadMap;
import cfh.taxi.TaxiException;
import cfh.taxi.Taxi;
import cfh.taxi.Path;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

// Go to [the] Writer: east 2nd left, 1st right.

public class GoToCommand extends Command {

    private final Location destination;
    private final Path path;
    
    public GoToCommand(int line, RoadMap map, Location destination, Path path) {
        super(line, map);
        if (destination == null) throw new IllegalArgumentException("null destination");
        if (path == null) throw new IllegalArgumentException("null path");
        
        this.destination = destination;
        this.path = path;
    }

    @Override
    public Result execute(Taxi taxi, InputOutput inpout) throws TaxiException {
        return taxi.travelTo(destination, path);
    }
    
    @Override
    public String toString() {
        return String.format("Go to %s: %s.", destination, path);
    }
}
