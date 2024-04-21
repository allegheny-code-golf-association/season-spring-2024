package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.*;

import java.util.EnumSet;
import java.util.Random;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class Heisenbergs extends Location {

    private transient final Random random = new Random();
    
    Heisenbergs(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "pickup an unspecified random integer";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(NUMERIC, INOUT);
    }
    
    @Override
    public Passenger pickupPassenger(InputOutput inpout) throws TaxiException {
        if (waiting.isEmpty()) {
            addOutgoing(new Passenger(random.nextInt()));
        }
        return super.pickupPassenger(inpout);
    }
}
