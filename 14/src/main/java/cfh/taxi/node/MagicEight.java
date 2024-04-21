package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.TEST;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class MagicEight extends Location {

    MagicEight(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "tests if the first passenger is less than the second \n" +
                "and returns the first if true or no one if not true, \n" +
                "non-numerical is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(TEST);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming.size() < 2)
            throw new TaxiException(this + " requires two passengers");
        Passenger first = incoming.removeFirst();
        Passenger second = incoming.removeFirst();
        double value1 = first.value.number(this);
        double value2 = second.value.number(this);
        if (value1 < value2) {
            addOutgoing(first);
        }
    }
}
