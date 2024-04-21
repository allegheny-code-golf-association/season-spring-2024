package cfh.taxi.cmd;

import cfh.taxi.Command;
import cfh.taxi.RoadMap;
import cfh.taxi.Taxi;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;

// [loop]

public class Label extends Command {

    private final String name;
    
    public Label(int line, RoadMap map, String name) {
        super(line, map);
        if (name.isEmpty()) throw new IllegalArgumentException("empty label");
        
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public Result execute(Taxi taxi, InputOutput inpout) {
        return null;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != Label.class) return false;
        
        Label other = (Label) obj;
        return name.equals(other.name);
    }
    
    @Override
    public String toString() {
        return "[" + name + "]";
    }
}
