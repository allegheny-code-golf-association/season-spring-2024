package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.CONVERSION;
import static cfh.taxi.node.NodeClass.NUMERIC;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class KnotsLanding extends Location {

    KnotsLanding(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "inverts boolean logic via numerical passengers: non-zero becomes 0, 0 becomes 1, \n" +
        	"non-numerical is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(NUMERIC, CONVERSION);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            double value = passenger.value.number(this);
            value = value == 0.0 ? 1.0 : 0.0;
            addOutgoing(new Passenger(value));
        }
    }
}
