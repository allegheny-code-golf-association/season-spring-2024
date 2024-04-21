package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.STRING;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class KonKats extends Location {

    KonKats(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "concatenates string passengers, anything non-string is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        if (incoming.isEmpty())
            return;
        StringBuilder result = new StringBuilder();
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            result.append(passenger.value.string(this));
        }
        addOutgoing(new Passenger(result.toString()));
    }
}
