package cfh.taxi.node;

import static cfh.taxi.Program.InputOutput.Level.DEBUG;
import static cfh.taxi.node.NodeClass.QUEUE;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.Taxi;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class RiverviewBridge extends Location {

    RiverviewBridge(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "passengers dropped off at Riverview Bridge seem to always fall \n" +
        	"over the side and into the river thus the driver collects no pay, \n" +
        	"but at least the pesky passenger is gone";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(QUEUE);
    }
    
    @Override
    protected void payment(Taxi taxi, Deque<Passenger> incoming) {
        // no payment
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inpout) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            inpout.log(DEBUG, "  %s falled into the river", passenger);
        }
    }
}
