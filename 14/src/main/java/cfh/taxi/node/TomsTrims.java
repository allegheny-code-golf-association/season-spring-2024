package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.STRING;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class TomsTrims extends Location {

    TomsTrims(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "removes whitespace from beginning and ending of string passengers, \n" +
        	"non-string is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            String text = passenger.value.string(this);
            addOutgoing(new Passenger(text.trim()));
        }
    }
}
