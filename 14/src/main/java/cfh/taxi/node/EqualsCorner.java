package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.TEST;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class EqualsCorner extends Location {

    EqualsCorner(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "tests if all dropped off numerical passengers are equal to each other, \n" +
        	"if so returns 1 passenger with the value, otherwise no passenger is returned, \n" +
        	"non-numeric is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(TEST);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming.size() < 2)
            throw new TaxiException(this + " requires at least 2 passengers");
        Passenger first = incoming.removeFirst();
        double firstValue = first.value.number(this);
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            double value = passenger.value.number(this);
            if (value != firstValue) {
                incoming.clear();
                return;
            }
        }
        addOutgoing(first);
    }
}
