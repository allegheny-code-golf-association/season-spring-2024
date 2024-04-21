package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.QUEUE;

import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;

public class RestBench extends Location {

    RestBench(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    @Override
    public String description() {
        return "one passenger can wait here until later, but only 1";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(QUEUE);
    }
    
    @Override
    protected boolean hasCapacity() {
        return waiting.isEmpty();
    }

    @Override
    public void addOutgoing(Passenger passenger) throws TaxiException {
        if (waiting.isEmpty()) {
            super.addOutgoing(passenger);
        } else {
            throw new TaxiException("there is already a passenger waiting at " + this);
        }
    }
}
