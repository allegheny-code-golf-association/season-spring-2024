package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.QUEUE;

import java.util.EnumSet;

import cfh.taxi.Location;

public class SunnySkiesPark extends Location {

    SunnySkiesPark(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "passengers dropped off here form a FIFO queue so they can be picked up again later";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(QUEUE);
    }
    
    @Override
    protected Queue createQueue() {
        return new FIFOQueue();
    }
}
