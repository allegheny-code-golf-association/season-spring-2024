package cfh.taxi;

import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

public abstract class Command {

    protected final int line;
    protected final RoadMap map;
    
    protected Command(int line, RoadMap map) {
        if (line < 0) throw new IllegalArgumentException("negative line number: " + line);
        if (map == null) throw new IllegalArgumentException("null map");
        
        this.line = line;
        this.map = map;
    }
    
    public abstract Result execute(Taxi taxi, InputOutput inpout) throws TaxiException;
}
