package cfh.taxi;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import cfh.taxi.node.NodeType;
import cfh.taxi.node.Rotatory;

public class RoadMap {
    
    private static final String NULL = "\0";
    
    private static final String KEY_SEP = "=";
    private static final String VERSION_KEY = "version";
    private static final String PATH_KEY = "path";
    private static final String START_KEY = "start";
    private static final String PIXELUNIT_KEY = "pixel/unit";
    private static final String MAXPASSENGERS_KEY = "maxPassengers";
    private static final String CAPACITY_KEY = "capacity";
    private static final String GASUSAGE_KEY = "gas-usage";
    private static final String FARE_KEY = "fare";
    
    private final BufferedImage image;
    private final String path;
    
    private int maxPassengers =  3;
    
    // 1 mile:                      264 pixels
    // Max gallons of gas:          20
    // Starting gallons:            20
    // miles per gallon:            18
    // starting credits:            0
    // fare in credits per mile:    0.07
    private double pixelUnit = 164;   // pixel/unit
    private double capacity = 75;   // gas
    private double gasUsage = 7.7;  // unit/gas 
    private double fare = 0.043;    // credits/unit
    
    private Location start;
    private final List<Node> nodes = new ArrayList<>();
    private transient final HashMap<String, Location> locations = new HashMap<>();
    
    public RoadMap(BufferedImage image, String path) {
        if (image == null) throw new IllegalArgumentException("null image");
        
        this.image = image;
        this.path = path;
    }

    public BufferedImage image() {
        return image;
    }
    
    public double pixelUnit() {
         return pixelUnit;
    }
    
    public void setPixelUnit(double pixelUnit) {
        if (pixelUnit <= 0.0) throw new IllegalArgumentException("pixelUnit not positive: " + pixelUnit);
        
        this.pixelUnit = pixelUnit;
    }
    
    public int maxPassengers() {
        return maxPassengers;
    }
    
    public void setMaxPassengers(int count) {
        if (count <= 0) throw new IllegalArgumentException("count not positive: " + count);
        
        maxPassengers = count;
    }
    
    public double capacity() {
        return capacity;
    }
    
    public void setCapacity(double capacity) {
        if (capacity <= 0.0) throw new IllegalArgumentException("capacity not positive: " + capacity);
        
        this.capacity = capacity;
    }
    
    public double gasUsage() {
        return gasUsage;
    }
    
    public void setGasUsage(double gasUsage) {
        if (gasUsage < 0.0) throw new IllegalArgumentException("gasUsage negative: " + gasUsage);
        
        this.gasUsage = gasUsage;
    }
    
    public double fare() {
        return fare;
    }
    
    public void setFare(double fare) {
        if (fare < 0.0) throw new IllegalArgumentException("negative fare: " + fare);
        
        this.fare = fare;
    }
    
    public List<Node> nodes() {
        return Collections.unmodifiableList(nodes);
    }
    
    public void addNode(Node node) {
        if (node == null) throw new IllegalArgumentException("null node");
        
        nodes.add(node);
        if (node instanceof Location) {
            Location location = (Location) node;
            locations.put(node.getName(), location);
            if (start == null && location.type() == NodeType.TAXI_GARAGE) {
                start = location;
            }
        }
    }
    
    public boolean removeNode(Node node) {
        if (node == null) throw new IllegalArgumentException("null node");
        
        if (start == node) {
            start = null;
        }
        if (node instanceof Location) {
            locations.remove(node.getName());
        }
        for (Direction dir : Direction.values()) {
            Node other = node.node(dir);
            if (other != null) {
                other.removeNode(node);
            }
        }
        return nodes.remove(node);
    }
    
    public void setStart(Location location) {
        if (location.type() != NodeType.TAXI_GARAGE) 
            throw new IllegalArgumentException("start must be a Garage: " + start);
        
        start = location;
    }
    
    public Location start() {
        return start;
    }
    
    public int waitingCount() {
        int result = 0;
        for (Node node : nodes) {
            result += node.waitingCount();
        }
        return result;
    }

    public void reset() {
        for (Node node : nodes) {
            node.reset();
        }
    }

    /*
     * 102: added pixel/unit.
     * 104: save NodeType as string.
     * 105: added path to RoadMap.
     * 106: save/load price for GasStation, removed FUELLER_UP, GO_MORE and ZOOM_ZOOM.
     * 107: added parameters to RoadMap.
     */
    private static final short VERSION = 107;
    
    public void save(OutputStream stream) throws IOException {
        DataOutputStream output = new DataOutputStream(stream);
        
        output.writeShort(VERSION);
        output.writeUTF(path != null ? path : NULL);
        output.writeUTF(start != null ? start.getName() : NULL);
        output.writeDouble(pixelUnit);
        output.writeInt(maxPassengers);
        output.writeDouble(capacity);
        output.writeDouble(gasUsage);
        output.writeDouble(fare);
        
        Node.writeNodes(nodes, output);
        ImageIO.write(image, "png", stream);
    }

    public static RoadMap load(InputStream stream) throws IOException {
        DataInputStream input = new DataInputStream(stream);
        
        short version = input.readShort();
        if (version < 101) {
            throw new UnsupportedOperationException("map is too old: " + version);
        } else if (version > VERSION) {
            throw new UnsupportedOperationException("program is too old to read version " + version);
        }
        
        String path = null;
        if (version >= 105) {
            path = input.readUTF();
            if (path.equals(NULL)) {
                path = null;
            }
        }
        String startName = input.readUTF();
        double pixelUnit = -1;
        if (version >= 102) {
            pixelUnit = input.readDouble();
        }
        int passengers = -1;
        double capacity = -1;
        double gasUsage = -1;
        double fare = -1;
        if (version >= 107) {
            passengers = input.readInt();
            capacity = input.readDouble();
            gasUsage = input.readDouble();
            fare = input.readDouble();
        }
        List<Node> nodes = Node.loadNodes(input, version);
        BufferedImage image = ImageIO.read(stream);
        RoadMap map = new RoadMap(image, path);
        if (version >= 102) {
            map.setPixelUnit(pixelUnit);
        }
        if (version >= 107) {
            map.setMaxPassengers(passengers);
            map.setCapacity(capacity);
            map.setGasUsage(gasUsage);
            map.setFare(fare);
        }
        for (Node node : nodes) {
            map.addNode(node);
        }
        if (!startName.equals(NULL)) {
            map.setStart(map.location(startName));
        }
        return map;
    }
    
    public void exportMap(OutputStream stream) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(stream))) {
            output.write(VERSION_KEY + KEY_SEP + VERSION);
            output.newLine();
            output.write(PATH_KEY + KEY_SEP + path);
            output.newLine();
            output.write(START_KEY + KEY_SEP + start);
            output.newLine();
            output.write(PIXELUNIT_KEY + KEY_SEP + pixelUnit);
            output.newLine();
            output.write(MAXPASSENGERS_KEY + KEY_SEP + maxPassengers);
            output.newLine();
            output.write(CAPACITY_KEY + KEY_SEP + capacity);
            output.newLine();
            output.write(GASUSAGE_KEY + KEY_SEP + gasUsage);
            output.newLine();
            output.write(FARE_KEY + KEY_SEP + fare);
            output.newLine();
            output.newLine();
            Node.exportNodes(nodes, output);
            output.flush();
        }
    }

    public static RoadMap importMap(InputStream stream) throws IOException {
        try (LineNumberReader input = new LineNumberReader(new InputStreamReader(stream))) {
            Map<String, String> values = new HashMap<String, String>();
            String line;
            while ((line = input.readLine()) != null) {
                if (line.trim().isEmpty())
                    break;
                String[] tokens = line.split("=", 2);
                if (tokens.length != 2)
                    throw new IOException(input.getLineNumber() + ": wrong format: " + line);
                if (values.containsKey(tokens[0]))
                    throw new IOException(input.getLineNumber() + ": " + tokens[0] + " duplicated");
                values.put(tokens[0], tokens[1]);
           }
            
            short version = Short.parseShort(values.get(VERSION_KEY));
            if (version < 105)
                throw new IOException(input.getLineNumber() + ": unsupported version: " + version);
            String path = values.get(PATH_KEY);
            if (path == null)
                throw new IOException(input.getLineNumber() + ": missing value for " + PATH_KEY);
            BufferedImage image = ImageIO.read(new File(path));
            RoadMap map = new RoadMap(image, path);
            String start = values.get(START_KEY);
            String str;
            str = values.get(PIXELUNIT_KEY);
            if (str != null) {
                map.setPixelUnit(Double.parseDouble(str));
            }
            str = values.get(MAXPASSENGERS_KEY);
            if (str != null) {
                map.setMaxPassengers(Integer.parseInt(str));
            }
            str = values.get(CAPACITY_KEY);
            if (str != null) {
                map.setCapacity(Double.parseDouble(str));
            }
            str = values.get(GASUSAGE_KEY);
            if (str != null) {
                map.setGasUsage(Double.parseDouble(str));
            }
            str = values.get(FARE_KEY);
            if (str != null) {
                map.setFare(Double.parseDouble(str));
            }
            List<Node> nodes = Node.importNodes(input);
            for (Node node : nodes) {
                map.addNode(node);
            }
            if (start != null) {
                map.setStart(map.location(start));
            }
            
            return map;
        }
    }
    
    public Location location(String name) {
        return locations.get(name);
    }

    public Node searchNode(int x, int y, int range) {
        Node result = null;
        double dist = 0;
        for (Node node : nodes) {
            double dx = node.x() - x;
            double dy = node.y() - y;
            double d = Math.sqrt(dx*dx + dy*dy);
            if (d <= range) {
                if (result == null || d < dist) {
                    result = node;
                    dist = d;
                }
            }
        }
        return result;
    }

    public double distance(Node from, Node dest) {
        if (from == null) throw new IllegalArgumentException("null from");
        if (dest == null) throw new IllegalArgumentException("null dest");
        
        double dx = dest.x() - from.x();
        double dy = dest.y()- from.y();
        return Math.sqrt(dx*dx + dy*dy) / pixelUnit;
    }
    
//TODO DELETE    public Path route(Node from, Node dest) {
    public List<Node> route(Node from, Node dest) {
        if (from == null) throw new IllegalArgumentException("null from");
        if (dest == null) throw new IllegalArgumentException("null dest");
        
        dmap.clear();
        Set<DNode> closed = new HashSet<DNode>();
        List<DNode> open = new ArrayList<DNode>();
        DNode first = new DNode(from, dest);
        first.g = 0;
        first.f = first.h;
        open.add(first);
        while (!open.isEmpty()) {
            DNode xx = null;
            for (DNode n : open) {
                if (xx == null || n.f < xx.f) {
                    xx = n;
                }
            }
            if (xx == null)
                break;
            if (xx.node == dest)
                return constructList(xx);
            open.remove(xx);
            closed.add(xx);
            for (Node y : xx.node.nodes) {
                if (y == null)
                    continue;
                DNode yy = dmap.get(y);
                if (closed.contains(yy))
                    continue;
                int dx = y.x - xx.node.x;
                int dy = y.y - xx.node.y;
                double tg = xx.g + Math.sqrt(dx*dx + dy*dy);
                if (yy == null || !open.contains(yy)) {
                    yy = new DNode(y, dest);
                    open.add(yy);
                } else if (tg >= yy.g) {
                    continue;
                }
                yy.prev = xx;
                yy.g = tg;
                yy.f = yy.g + yy.h;
            }
        }
        return null;
    }

    private static List<Node> constructList(DNode dest) {
        assert dest != null;
        
        LinkedList<Node> list = new LinkedList<>();
        for (DNode n = dest; n != null; n = n.prev) {
            list.addFirst(n.node);
        }
        return Collections.unmodifiableList(list);
    }
    
    @SuppressWarnings("null")
    public static Path constructPath(List<Node> nodes) {
        if (nodes.size() < 2)
            return null;
        Node first = nodes.get(0);
        Node second = nodes.get(1);
        Path result = null;
        for (Direction dir : Direction.values()) {
            if (first.node(dir) == second) {
                result = new Path(dir);
                break;
            }
        }
        if (result == null)
            return null;
        Node last = null;
        Node curr = null;
        int left = 0;
        int right = 0;
        for (Node next : nodes) {
            if (last != null) {
                Direction dir = null;
                for (Direction d : Direction.values()) {
                    if (curr.node(d) == last) {
                        dir = d.opposite();
                        break;
                    }
                }
                if (curr instanceof Rotatory) {
                    Node rightNode = last;
                    Node leftNode = last;
                    while (true) {
                        Direction rightDir = curr.getRight(rightNode);
                        Direction leftDir = curr.getLeft(leftNode);
                        rightNode = curr.node(rightDir);
                        leftNode = curr.node(leftDir);
                        if (rightNode == curr && leftNode == curr) {
                            result.addDeadEnd();
                            break;
                        }
                        if (rightNode == next) {
                            result.addRightInstruction(right+1);
                            break;
                        } else if (leftNode == next) {
                            result.addLeftInstruction(left+1);
                            break;
                        }
                        if (rightNode != curr) {
                            if (rightNode != null)
                                right += 1;
                            rightDir = rightDir.left();
                        }
                        if (leftNode != curr) {
                            if (leftNode != null)
                                left += 1;
                            leftDir = leftDir.right();
                        }
                    }
                    left = 0;
                    right = 0;
                } else if (curr instanceof Rotatory) {
                    // TODO  subst  and DELETE
                    Direction rightDir = dir.right();
                    Direction leftDir = dir.left();
                    while (true) {
                        Node rightNode;
                        Node leftNode;
                        do {
                            rightNode = curr.node(rightDir);
                            rightDir = rightDir.left();
                        } while (rightNode == null);
                        do {
                            leftNode = curr.node(leftDir);
                            leftDir = leftDir.right();
                        } while (leftNode == null);
                        if (rightNode == curr && leftNode == curr) {
                            result.addDeadEnd();
                            break;
                        }
                        if (rightNode == next) {
                            result.addRightInstruction(right+1);
                            break;
                        } else if (leftNode == next) {
                            result.addLeftInstruction(left+1);
                            break;
                        }
                        if (rightNode != curr) {
                            if (rightNode != null)
                                right += 1;
                            rightDir = rightDir.left();
                        }
                        if (leftNode != curr) {
                            if (leftNode != null)
                                left += 1;
                            leftDir = leftDir.right();
                        }
                    }
                    left = 0;
                    right = 0;
                } else if (!curr.isCorner()) {
                    Node leftNode = curr.node(dir.left());
                    Node rightNode = curr.node(dir.right());
                    if (leftNode == next) {
                        result.addLeftInstruction(left+1);
                        left = 0;
                        right = 0;
                    } else if (rightNode == next) {
                        result.addRightInstruction(right+1);
                        left = 0;
                        right = 0;
                    } else {
                        if (leftNode != null)
                            left += 1;
                        if (rightNode != null)
                            right += 1;
                    }
                }
            }
            last = curr;
            curr = next;
        }
        return result;
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Map<Node, DNode> dmap = new HashMap<>();

    private class DNode {
        private final Node node;
        private double f;
        private double g;
        private final double h;
        private DNode prev;
        
        private DNode(Node node, Node dest) {
            assert node != null;
            assert dest != null;
            
            this.node = node;
            double dx = dest.x - node.x;
            double dy = dest.y - node.y;
            h = Math.sqrt(dx*dx + dy*dy);
            dmap.put(node, this);
        }

        @Override
        public String toString() {
            return String.format("%s[%.2f,%.2f,%s]", node.getName(), f, g, prev!=null?prev.node:null);
        }
    }
}
