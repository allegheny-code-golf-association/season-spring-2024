package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.INOUT;
import static cfh.taxi.node.NodeClass.STRING;

import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Value;

public class WritersDepot extends Location {

    WritersDepot(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    @Override
    public String description() {
        return "pickup a specified string";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING, INOUT);
    }

    @Override
    public Passenger addWaiting(Value value) throws TaxiException {
        if (!value.isString())
            throw new TaxiException(value + " cannot be made to wait at " + this + ", only strings");
        Passenger passenger = new Passenger(value);
        addOutgoing(passenger);
        return passenger;
    }
}
