package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.QUEUE;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class Cyclone extends Location {

    Cyclone(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "makes clones of passengers, drop 1 off, get original plus 1 copy back, \n" +
        	"drop 3 off, get original 3, plus 3 copies back, etc.";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(QUEUE);
    }

    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            addOutgoing(passenger);
            addOutgoing(new Passenger(passenger));
        }
    }
}
