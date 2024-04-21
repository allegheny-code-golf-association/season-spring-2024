package cfh.taxi.node;

import static cfh.taxi.node.NodeClass.LAYOUT;

import java.util.EnumSet;

import cfh.taxi.Direction;
import cfh.taxi.Node;

public class Rotatory extends Node {

    protected Rotatory(NodeType type, String name, int x, int y) {
        super(type, name, x, y);
    }

    @Override
    public String description() {
        return "A rotatory for streets";
    }
    
    @Override
    public EnumSet<NodeClass> nodeClass() {
        return EnumSet.of(LAYOUT);
    }

    @Override
    public Direction getRight(Node from) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == from) {
                for (int j = 1; j <= 4; j++) {
                    int d = (i-j+4) % 4;
                    if (nodes[d] != null)
                        return Direction.values()[d];
                }
            }
        }
        throw new IllegalArgumentException("No connection to node " + from);
    }

    @Override
    public Direction getLeft(Node from) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == from) {
                for (int j = 1; j <= 4; j++) {
                    int d = (i+j) % 4;
                    if (nodes[d] != null)
                        return Direction.values()[d];
                }
            }
        }
        throw new IllegalArgumentException("No connection to node " + from);
    }
    
    @Override
    public String toString() {
        return "rotatory " + name;
    }
}
