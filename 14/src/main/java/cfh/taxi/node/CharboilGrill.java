package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.CONVERSION;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Value;
import cfh.taxi.Program.InputOutput;

public class CharboilGrill extends Location {

    CharboilGrill(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "convert a numerical passenger to a single ASCII character (string) or vice-versa, \n" +
        	"strings longer than 1 are an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(CONVERSION);
    }

    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            Value outgoing;
            if (passenger.value.isNumber()) {
                int value = (int) passenger.value.number(this);
                outgoing = Value.createValue(Character.toString((char)value));
            } else if (passenger.value.isString()) {
                String value = passenger.value.string(this);
                if (value.length() != 1)
                    throw new TaxiException(this + " can only handle strings of length 1");
                outgoing = Value.createValue(value.charAt(0));
            } else {
                throw new TaxiException(passenger.value + " is an unknown data type at " + this);
            }
            addOutgoing(new Passenger(outgoing));
        }
    }
}
