package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.TAXI;

import java.util.EnumSet;

import cfh.taxi.Location;
import cfh.taxi.Taxi;
import cfh.taxi.Program.Result;
import cfh.taxi.Program.TheEnd;

public class TaxiGarage extends Location {

    TaxiGarage(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "starting and termination point";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(TAXI);
    }

    @Override
    protected Result arrived(Taxi taxi) {
        return new TheEnd("The taxi is back in the garage.");
    }
}
