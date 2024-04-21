package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.QUEUE;

import java.util.EnumSet;

import cfh.taxi.Location;

public class FiremouthGrill extends Location {

    FiremouthGrill(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "any number of passengers can be dropped off here, \n" +
        	"but picked up in random and unknown order";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(QUEUE);
    }
    
    @Override
    protected Queue createQueue() {
        return new RANDOMQueue();
    }
}
