package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.LAYOUT;

import java.util.EnumSet;

import cfh.taxi.Node;

public class Intersection extends Node {

    Intersection(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }
    
    @Override
    public String description() {
        return "an intersection of streets";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(LAYOUT);
    }
    
    @Override
    public String toString() {
        return "intersection " + name;
    }
}
