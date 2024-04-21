package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.CONVERSION;
import static cfh.taxi.node.NodeClass.STRING;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.Program.InputOutput;
import cfh.taxi.TaxiException;

public class ChopSuey extends Location {

    ChopSuey(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "takes a string passenger and breaks it up into individual string passengers \n" +
        	"that hold one character each, so \"Hi\" results in 2 passengers: \"H\" and \"i\", \n" +
        	"non-string is an error";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING, CONVERSION);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            String value = passenger.value.string(this);
            for (int i = 0; i < value.length(); i++) {
                addOutgoing(new Passenger(value.substring(i, i+1)));
            }
        }
    }
}
