package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.NUMERIC;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class WhatsTheDifference extends Location {

    WhatsTheDifference(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "subtracts numerical passengers, anything non-numeric is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(NUMERIC);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming.isEmpty())
            return;
        double result = incoming.removeFirst().value.number(this);
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            result -= passenger.value.number(this);
        }
        addOutgoing(new Passenger(result));
    }
}
