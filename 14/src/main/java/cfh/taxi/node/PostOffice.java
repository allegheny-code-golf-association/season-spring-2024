package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.INOUT;
import static cfh.taxi.node.NodeClass.STRING;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class PostOffice extends Location {

    protected PostOffice(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "drop off string passengers to print to stdout, \n" +
        	"pickup a passenger to read a string line from input";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING, INOUT);
    }
    
    @Override
    public Passenger pickupPassenger(InputOutput output) throws TaxiException {
        if (waiting.isEmpty()) {
            String value;
            value = output.readLine();
            if (value != null) {
                addOutgoing(new Passenger(value));
            }
        }
        return super.pickupPassenger(output);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            inpout.print(passenger.value.string(this));
        }
    }
}
