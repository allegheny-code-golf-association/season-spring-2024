package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.CONVERSION;
import static cfh.taxi.node.NodeClass.STRING;

import java.util.Deque;
import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.InputOutput;

public class LittleLeagueField extends Location {

    LittleLeagueField(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    @Override
    public String description() {
        return "converts string passengers to lowercase, non-string is an error ";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(STRING, CONVERSION);
    }
    
    @Override
    protected void receive(Deque<Passenger> incoming, InputOutput inoput) throws TaxiException {
        while (!incoming.isEmpty()) {
            Passenger passenger = incoming.removeFirst();
            String text = passenger.value.string(this);
            addOutgoing(new Passenger(text.toLowerCase()));
        }
    }
}
