package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.INOUT;
import static cfh.taxi.node.NodeClass.NUMERIC;

import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Value;


public class StarchildNumerology extends Location {

    protected StarchildNumerology(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "pickup a specified numerical value";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(NUMERIC, INOUT);
    }

    @Override
    public Passenger addWaiting(Value value) throws TaxiException {
        if (!value.isNumber())
            throw new TaxiException(value + " cannot be made to wait at " + this + ", only numerical values");
        Passenger passenger = new Passenger(value);
        addOutgoing(passenger);
        return passenger;
    }
}
