package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.TAXI;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cfh.taxi.Location;
import cfh.taxi.Passenger;
import cfh.taxi.Taxi;
import cfh.taxi.TaxiException;
import cfh.taxi.Program.Result;

public class GasStation extends Location {

    private static final String PRICE_KEY = "price";
    
    private int price;  // cents/ltr
    
    GasStation(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    public void setPrice(int price) {
        if (price <= 0)
            throw new IllegalArgumentException("price not positive: " + price);
        
        this.price = price;
    }
    
    @Override
    public String description() {
        return String.format("gas station: %.2f/ltr", price / 100.0);
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(TAXI);
    }
    
    @Override
    protected boolean hasCapacity() {
        return false;
    }
    @Override
    public void addOutgoing(Passenger passenger) throws TaxiException {
        throw new TaxiException("no outgoing passengers accepted at " + this);
    }
    
    @Override
    protected Result arrived(Taxi taxi) {
        taxi.refuel(price);
        return super.arrived(taxi);
    }
    
    @Override
    public void askDetail(JFrame frame) {
        super.askDetail(frame);
        String message = "Price [credits/KM]:";
        while (true) {
            String input = JOptionPane.showInputDialog(frame, message, price/100.0);
            if (input == null || input.isEmpty())
                break;
            try {
                price = (int) Math.round(Double.parseDouble(input)*100.0);
                break;
            } catch (NumberFormatException ex) {
                message = ex + "\nPrice [credits/KM]:";
            }
        }
    }
    
    @Override
    protected void write(DataOutputStream output) throws IOException {
        super.write(output);
        output.writeInt(price);
    }
    
    @Override
    protected void load(DataInputStream input, int version) throws IOException {
        super.load(input, version);
        if (version >= 106) {
            price = input.readInt();
        }
    }
    
    @Override
    protected void exportNode(BufferedWriter output) throws IOException {
        super.exportNode(output);
        output.write(PRICE_KEY + "=" + price);
        output.newLine();
    }
    
    @Override
    protected void importNode(Map<String, String> values) {
        super.importNode(values);
        String val = values.get(PRICE_KEY);
        price = Integer.parseInt(val);
    }
}
