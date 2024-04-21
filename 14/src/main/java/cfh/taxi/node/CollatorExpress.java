package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.TEST;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class CollatorExpress extends Location {

    CollatorExpress(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "tests if the first string passenger is less than the second \n" +
        	"and returns the first if true or no one if not true, non-string is an error";
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
        String value1 = first.value.string(this);
        String value2 = second.value.string(this);
        if (value1.compareTo(value2) < 0) {
            addOutgoing(first);
        }
    }
}
