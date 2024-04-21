package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.NUMERIC;
import static cfh.taxi.node.NodeClass.TEST;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class TheUnderground extends Location {

    TheUnderground(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "takes 1 numerical passenger and subtracts 1, \n" +
        	"if the result is 0 or less than 0, no passenger is returned \n" +
        	"otherwise the result is returned, non-numerical is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(NUMERIC, TEST);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming.isEmpty())
            return;
        double value = incoming.removeFirst().value.number(this);
        value -= 1;
        if (value > 0) {
            addOutgoing(new Passenger(value));
        }
    }
}
