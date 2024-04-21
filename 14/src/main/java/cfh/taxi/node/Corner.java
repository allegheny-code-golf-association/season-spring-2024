package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.LAYOUT;

import java.util.EnumSet;

import cfh.taxi.Node;

public class Corner extends Node {

    Corner(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    @Override
    public String description() {
        return "just a corner on the street";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(LAYOUT);
    }
    
    @Override
    public boolean canConnect(int cx, int cy) {
        if (!super.canConnect(cx, cy))
            return false;
        return getConnectionCount() < 2;
    }

    @Override
    public boolean isCorner() {
        return true;
    }
    
    @Override
    public String toString() {
        return "corner " + name;
    }
}
