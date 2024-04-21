package cfh.taxi.cmd;

import static cfh.taxi.Program.InputOutput.Level.*;

import cfh.taxi.Command;
import cfh.taxi.RoadMap;
import cfh.taxi.TaxiException;
import cfh.taxi.Taxi;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.Program.Result;
import cfh.taxi.Program.JumpLabel;

// Switch to plan "loop".
// Switch to plan "loop" if no one is waiting.

public class SwitchTo extends Command {

    private final String label;
    private final boolean conditional;
    
    public SwitchTo(int line, RoadMap map, String label, boolean conditional) {
        super(line, map);
        if (label.isEmpty()) throw new IllegalArgumentException("empty label");
        
        this.label = label;
        this.conditional = conditional;
    }

    @Override
    public Result execute(Taxi taxi, InputOutput inpout) throws TaxiException {
        String msg;
        Result result;
        if (conditional) {
            if (taxi.location().isEmpty()) {
                msg = "Switching to plan \"%s\" since nobody is waiting";
                result = new JumpLabel(label);
            } else {
                msg = "Not switching to plan \"%s\" because someone is waiting";
                result = null;
            }
        } else {
            msg = "Switching to plan \"%s\"";
            result = new JumpLabel(label);
        }
        inpout.log(PRINT, msg, label);
        return result;
    }
    
    @Override
    public String toString() {
        if (conditional)
            return "Switch to plan \"" + label + "\" if no one is waiting.";
        else
            return "Switch to plan \"" + label + "\".";
    }
}
