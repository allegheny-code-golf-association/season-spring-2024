package cfh.taxi;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import cfh.taxi.node.GasStation;
import cfh.taxi.node.NodeClass;
import cfh.taxi.node.NodeType;

public abstract class Node {

    private static final String NULL = "\0";
    
    private static final String KEY_SEP = "=";
    private static final String NODE_START_KEY = "node";
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";
    private static final String POSITION_KEY = "position";
    private static final String NODES_KEY = "nodes";
    
    public static void writeNodes(List<Node> nodes, DataOutputStream output) throws IOException {
        if (nodes == null) throw new IllegalArgumentException("null nodes");
        if (output == null) throw new IllegalArgumentException("null output");
        
        Map<Node, Integer> map = new HashMap<Node, Integer>();
        for (int i = 0; i < nodes.size(); i++) {
            map.put(nodes.get(i), i);
        }
        output.writeInt(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            output.writeUTF(node.type().name());
            output.writeUTF(node.name != null ? node.name : NULL);
            output.writeInt(node.x);
            output.writeInt(node.y);
            for (int j = 0; j < node.nodes.length; j++) {
                Node n = node.nodes[j];
                if (n != null) {
                    output.writeInt(map.get(n).intValue());
                } else {
                    output.writeInt(-1);
                }
            }
            node.write(output);
        }
    }

    public static List<Node> loadNodes(DataInputStream input, int version) throws IOException {
        if (input == null) throw new IllegalArgumentException("null input");
        
        List<Node> nodes = new ArrayList<>();
        int size = input.readInt();
        int[][] ref = new int[size][4];
        for (int i = 0; i < size; i++) {
            NodeType type;
            int gasPrice = -1;
            if (version == 103) {
                int ordinal = input.readInt();
                switch (ordinal) {
                    case 0: type = NodeType.CORNER; break;
                    case 1: type = NodeType.INTERSECTION; break;
                    case 2: type = NodeType.TAXI_GARAGE; break;
                    case 3: type = NodeType.WRITERS_DEPOT; break;
                    case 4: type = NodeType.POST_OFFICE; break;
                    default: throw new IOException("unknown NodeType ordinal: " + ordinal);
                }
            } else {
                String name = input.readUTF();
                if (version < 106) {
                    if (name.equals("FUELER_UP")) {
                        name = NodeType.GAS_STATION.name();
                        gasPrice = 146;
                    } else if (name.equals("GO_MORE")) {
                        name = NodeType.GAS_STATION.name();
                        gasPrice = 133;
                    } else if (name.equals("ZOOM_ZOOM")) {
                        name = NodeType.GAS_STATION.name();
                        gasPrice = 110;
                    }
                }
                type = NodeType.valueOf(name);
            }
            String name = input.readUTF();
            if (name.equals(NULL)) {
                name = null;
            }
            int x = input.readInt();
            int y = input.readInt();
            for (int j = 0; j < 4; j++) {
                ref[i][j] = input.readInt();
            }
            Node node = type.createNode(name, x, y);
            if (version < 106 && node instanceof GasStation) {
                ((GasStation) node).setPrice(gasPrice);
            }
            nodes.add(node);
            if (version >= 103) {
                node.load(input, version);
            }
        }
        for (int i = 0; i < size; i++) {
            Node node = nodes.get(i);
            for (int j = 0; j < node.nodes.length; j++) {
                if (ref[i][j] != -1) {
                    node.nodes[j] = nodes.get(ref[i][j]);
                }
            }
        }
        return nodes;
    }
    
    public static void exportNodes(List<Node> nodes, BufferedWriter output) throws IOException {
        if (nodes == null) throw new IllegalArgumentException("null nodes");
        if (output == null) throw new IllegalArgumentException("null output");

        Map<Node, Integer> map = new HashMap<Node, Integer>();
        for (int i = 0; i < nodes.size(); i++) {
            map.put(nodes.get(i), i);
        }
        
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            output.write(NODE_START_KEY + KEY_SEP + i);
            output.newLine();
            output.write(NAME_KEY + KEY_SEP + (node.name != null ? node.name : NULL));
            output.newLine();
            output.write(TYPE_KEY + KEY_SEP + node.type.name());
            output.newLine();
            output.write(String.format("%s%s%d,%d", POSITION_KEY, KEY_SEP, node.x, node.y));
            output.newLine();
            StringBuilder builder = new StringBuilder(NODES_KEY);
            String separator = KEY_SEP;
            for (Node n : node.nodes) {
                builder.append(separator);
                separator = ",";
                if (n != null) {
                    builder.append(map.get(n).intValue());
                }
            }
            output.write(builder.toString());
            output.newLine();
            node.exportNode(output);
            output.newLine();
        }
    }
    
    public static List<Node> importNodes(LineNumberReader input) throws IOException {
        if (input == null) throw new IllegalArgumentException("null input");
        
        List<Node> result = new ArrayList<>();
        Map<Integer, Node> nodes = new HashMap<Integer, Node>();
        Map<Integer, String> refs = new HashMap<Integer, String>();
        String line;
        while ((line = input.readLine()) != null) {
            if (line.trim().isEmpty())
                continue;
            if (!line.startsWith(NODE_START_KEY))
                throw new IOException(input.getLineNumber() + ": wrong node start: " + line);
            int number = Integer.parseInt(line.substring(NODE_START_KEY.length() + KEY_SEP.length()));
            Map<String, String> values = new HashMap<String, String>();
            while ((line = input.readLine()) != null) {
                if (line.trim().isEmpty())
                    break;
                String[] tokens = line.split("=", 2);
                if (tokens.length != 2)
                    throw new IOException(input.getLineNumber() + ": wrong format for node " + number + ": " + line);
                if (values.containsKey(tokens[0]))
                    throw new IOException(input.getLineNumber() + ": " + tokens[0] + " duplicated: " + line);
                values.put(tokens[0], tokens[1]);
            }
            String name = values.get(NAME_KEY);
            NodeType type = NodeType.valueOf(values.get(TYPE_KEY));
            String[] pos = values.get(POSITION_KEY).split(",", 2);
            if (pos.length != 2)
                throw new IOException(input.getLineNumber() + ": wrong coordinate format for node " + number + ": " + line);
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            Node node = type.createNode(name, x, y);
            node.importNode(values);
            result.add(node);
            nodes.put(number, node);
            refs.put(number, values.get(NODES_KEY));
        }
        for (Integer number : nodes.keySet()) {
            Node node = nodes.get(number);
            String[] ref = refs.get(number).split(",", -1);
            for (int i = 0; i < ref.length; i++) {
                if (!ref[i].isEmpty()) {
                    int j = Integer.parseInt(ref[i]);
                    node.nodes[i] = nodes.get(j);
                }
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected final NodeType type;
    protected final String name;
    protected int x;
    protected int y;
    
    protected final Node[] nodes = new Node[4];  // N E S W (clockwise)
    
    protected Node(NodeType type, String name, int x, int y) {
        if (type == null) throw new IllegalArgumentException("null type");
        
        this.type = type;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public final NodeType type() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }

    public void setCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract EnumSet<NodeClass> nodeClass();
    
    public abstract String description();
    
    protected void write(DataOutputStream output) throws IOException {
    }

    protected void load(DataInputStream input, int version) throws IOException {
    }
    
    public Node node(Direction direction) {
        return nodes[direction.ordinal()];
    }
    
    public Direction getRight(Node from) {
        if (from == null) throw new IllegalArgumentException("null from");
        
        for (Direction dir : Direction.values()) {
            if (nodes[dir.ordinal()] == from) {
                return dir.opposite().right();
            }
        }
        throw new IllegalArgumentException("No connection to node " + from);
    }
    
    public Direction getLeft(Node from) {
        if (from == null) throw new IllegalArgumentException("null from");
        
        for (Direction dir : Direction.values()) {
            if (nodes[dir.ordinal()] == from) {
                return dir.opposite().left();
            }
        }
        throw new IllegalArgumentException("No connection to node " + from);
    }
    
    public boolean canConnect(int cx, int cy) {
        int dx = cx - x;
        int dy = cy - y;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (cx > x)
                return node(Direction.EAST) == null;
            else
                return node(Direction.WEST) == null;
        } else {
            if (cy > y)
                return node(Direction.SOUTH) == null;
            else
                return node(Direction.NORTH) == null;
        }
    }
    
    public boolean connect(Node node, int cx, int cy) {
        if (node == null) throw new IllegalArgumentException("null node");
        
        if (!canConnect(cx, cy))
            return false;
        Direction dir = getDirection(cx, cy);
        nodes[dir.ordinal()] = node;
        return true;
    }
    
    public Node disconnect(int cx, int cy) {
        Direction dir = getDirection(cx, cy);
        Node node = nodes[dir.ordinal()];
        nodes[dir.ordinal()] = null;
        return node;
    }
    
    public Direction removeNode(Node node) {
        if (node == null) throw new IllegalArgumentException("null node");
        
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == node) {
                nodes[i] = null;
                return Direction.values()[i];
            }
        }
        return null;
    }

    public void reset() {
    }

    protected int getConnectionCount() {
        int count = 0;
        for (Node node : nodes) {
            if (node != null)
                count += 1;
        }
        return count;
    }

    public boolean isCorner() {
        return false;
    }
    
    public Direction getDirection(int cx, int cy) {
        int dx = cx - x;
        int dy = cy - y;
        if (Math.abs(dx) > Math.abs(dy)) {
            if (cx > x)
                return Direction.EAST;
            else
                return Direction.WEST;
        } else {
            if (cy > y)
                return Direction.SOUTH;
            else
                return Direction.NORTH;
        }
    }
    
    protected void exportNode(BufferedWriter output) throws IOException {
    }

    protected void importNode(Map<String, String> values) {
    }

    public void askDetail(JFrame frame) {
    }

    public int waitingCount() {
        return 0;
    }

    public List<Passenger> waiting() {
        return Collections.emptyList();
    }
}
