package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.CONVERSION;

import java.util.Deque;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.TaxiException;
import cfh.taxi.Value;
import cfh.taxi.Program.InputOutput;

public class TheBabelfishery extends Location {

    TheBabelfishery(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "translates a numerical passenger to a string passenger or vice-versa";
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
                double value = passenger.value.number(this);
                if (value == (long) value) {
                    outgoing = Value.createValue(Long.toString((long) value)); 
                } else {
                    outgoing = Value.createValue(Double.toString(value)); 
                }
            } else if (passenger.value.isString()) {
                /*
                 * An optional plus or minus sign; 
                 * a sequence of digits, optionally containing a decimal-point character;
                 * an optional exponent part, which itself consists on an 'e' or 'E' character 
                 * followed by an optional sign and a sequence of digits.
                 */
                String value = passenger.value.string(this);
                Pattern pattern = Pattern.compile("\\s*((\\+|-)?(\\d+(\\.\\d*)?|\\.\\d+)((e|E)(\\+|-)?\\d+)?)");
                Matcher matcher = pattern.matcher(value);
                if (matcher.lookingAt()) {
                    try {
                        outgoing = Value.createValue(Double.parseDouble(matcher.group(1)));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        outgoing = Value.createValue(0.0);
                    }
                } else {
                    outgoing = Value.createValue(0.0);
                }
            } else {
                throw new TaxiException(passenger.value + " is an unknown data type at " + this);
            }
            addOutgoing(new Passenger(outgoing));
        }
    }
}
