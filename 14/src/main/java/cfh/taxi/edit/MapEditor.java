package cfh.taxi.edit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import cfh.taxi.Node;
import cfh.taxi.RoadMap;
import cfh.taxi.gui.Main;
import cfh.taxi.node.NodeType;

public class MapEditor {
    
    private static final int RANGE = 20;
    
    private static final String PREF_IMG_DIR = "imageDiretory";
    private static final String PREF_MAP_DIR = "mapDirectory";
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    
    private final JFrame frame;
    private final JLabel coords;
    private final ImagePanel imagePanel;
    private final JToggleButton dists;
    private final JToggleButton nodes;
    private final JToggleButton streets;
    
    private final NodeDialog nodeDialog;
    
    private RoadMap map = null;
    
    public static void main(String[] args) {
        new MapEditor();
    }
    
    private MapEditor() {
        
        coords = new JLabel("9999,9999");
        Dimension size = coords.getPreferredSize();
        size.width = 70;
        coords.setPreferredSize(size);

        JButton newMap = newJButton("New", 
                "Start a new map with a background image from file; <CTRL> create new map with empty background");
        newMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0;
                doNewMap(ctrl);
            }
        });
        
        dists = newJToggleButton("Dist");
        dists.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                imagePanel.setMark(null);
                imagePanel.setRuler(null);
            }
        });
        dists.setToolTipText("Mark 2 points and enter their distance");
        
        nodes = newJToggleButton("Node");
        nodes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                imagePanel.setMark(null);
                imagePanel.setRuler(null);
            }
        });
        nodes.setToolTipText("Edit nodes: click for new or edit existing node; CTRL to delete");
        
        streets = newJToggleButton("Street");
        streets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imagePanel.setCursor(Cursor.getDefaultCursor());
                imagePanel.setMark(null);
                imagePanel.setRuler(null);
            }
        });
        streets.setToolTipText("Edit streets: connect nodes by clicking");
        
        ButtonGroup modes = new ButtonGroup();
        modes.add(dists);
        modes.add(nodes);
        modes.add(streets);
        
        JButton save = newJButton("Save", "Save the map); <CTRL> export the map (text file)");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0; 
                doSave(ctrl);
            }
        });
        
        JButton load = newJButton("Load", "Loade a map); <CTRL> import a map (text file)");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ctrl = (e.getModifiers() & ActionEvent.CTRL_MASK) != 0; 
                doLoad(ctrl);
            }
        });
        
        JButton param = newJButton("Param", "Edit the map parameters");
        param.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doParam();
            }
        });
        
        JButton quit = newJButton("QUIT", "Just ends the IDE");
        quit.setForeground(Color.RED);
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doQuit();
            }
        });
        
        Box buttons = Box.createHorizontalBox();
        buttons.add(coords);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(newMap);
        buttons.add(Box.createHorizontalStrut(4));
        buttons.add(load);
        buttons.add(save);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(param);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(dists);
        buttons.add(nodes);
        buttons.add(streets);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(quit);
        buttons.add(Box.createHorizontalGlue());
        
        frame = new JFrame("Map Editor for " + Main.TITLE);
        
        imagePanel = new ImagePanel();
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mods = e.getModifiersEx();
                if ((mods & MouseEvent.CTRL_DOWN_MASK) != 0) {
                    onMouseClickedCtrl(e);
                } else {
                    onMouseClicked(e);
                }
            }
        });
        imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                coords.setText(String.format("%4d, %4d", e.getX(), e.getY()));
            }
        });
        
        nodeDialog = new NodeDialog(frame);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(buttons, BorderLayout.BEFORE_FIRST_LINE);
        frame.add(new JScrollPane(imagePanel));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void doNewMap(boolean ctrl) {
        BufferedImage image;
        String path;
        if (ctrl) {
            String text;
            text = JOptionPane.showInputDialog(frame, "Size for new Map [x,y]:");
            Dimension size = parseDimension(text);
            if (size == null) return;
            text = JOptionPane.showInputDialog(frame, "Step [x,y]:");
            Dimension step = parseDimension(text);
            if (step == null) return;
            image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D gg = (Graphics2D) image.getGraphics();
            try {
                gg.setColor(new Color(255, 218, 88));
                gg.fillRect(0, 0, size.width, size.height);
                gg.setColor(Color.black);
                for (int x = 0; x < size.width; x += step.width) {
                    gg.drawLine(x, 0, x, size.height);
                }
                for (int y = 0; y < size.height; y += step.height) {
                    gg.drawLine(0, y, size.width, y);
                }
            } finally {
                gg.dispose();
            }
            path = "created " + size;
        } else {
            path = prefs.get(PREF_IMG_DIR, ".");
            JFileChooser chooser = new JFileChooser(path);
            if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
                return;

            path = chooser.getCurrentDirectory().getAbsolutePath();
            prefs.put(PREF_IMG_DIR, path);
            File file = chooser.getSelectedFile();
            try {
                image = ImageIO.read(file);
                if (image == null)
                    throw new IOException("unable to open image from " + file);
                path = file.getPath();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        map = new RoadMap(image, path);
        imagePanel.setRoadMap(map);
    }
    
    private Dimension parseDimension(String text) throws NumberFormatException {
        String[] tokens = text.trim().split("\\s*[x, ]\\s*", 2);
        int width = Integer.parseInt(tokens[0]);
        int height = (tokens.length == 2) ? Integer.parseInt(tokens[1]) : width;
        if (width < 1 || height < 1) {
            JOptionPane.showMessageDialog(frame, "invalid dimension: " + text, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Dimension(width, height); 
    }
    
    private void doSave(boolean ctrl) {
        if (map == null)
            return;
        String path = prefs.get(PREF_MAP_DIR, ".");
        JFileChooser chooser = new JFileChooser(path);
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;
        
        path = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_MAP_DIR, path);
        File file = chooser.getSelectedFile();
        
        if (file.exists()) {
            if (JOptionPane.showConfirmDialog(
                    frame, 
                    file.getName() + " already exists. Overwrite?", 
                    "Confrim", 
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                return;
        }
        
        try (OutputStream out = new FileOutputStream(file)) {
            if (ctrl) {
                map.exportMap(out);
            } else {
                map.save(out);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doLoad(boolean ctrl) {
        String path = prefs.get(PREF_MAP_DIR, ".");
        JFileChooser chooser = new JFileChooser(path);
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
            return;
        
        path = chooser.getCurrentDirectory().getAbsolutePath();
        prefs.put(PREF_MAP_DIR, path);
        File file = chooser.getSelectedFile();
        try (InputStream inp = new FileInputStream(file)) {
            if (ctrl) {
                map = RoadMap.importMap(inp);
            } else {
                map = RoadMap.load(inp);
            }
            imagePanel.setRoadMap(map);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "IOException", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doParam() {
        if (map == null) return;
        ParamDialog dialog = new ParamDialog(
                frame, map.pixelUnit(), map.maxPassengers(), map.capacity(), map.gasUsage(), map.fare());
        if (dialog.showDialog()) {
            map.setPixelUnit(dialog.pixel());
            map.setMaxPassengers(dialog.passengers());
            map.setCapacity(dialog.maxTank());
            map.setGasUsage(dialog.gasUsage());
            map.setFare(dialog.fare());
        }
    }
    
    private void doQuit() {
        int option = JOptionPane.showConfirmDialog(frame, "Really quit?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            frame.dispose();
        }
    }

    private void onMouseClicked(MouseEvent e) {
        if (map == null) return;
        int x = e.getX();
        int y = e.getY();
        if (dists.isSelected()) {
            Point mark = imagePanel.mark();
            if (mark == null) {
                imagePanel.setMark(e.getPoint());
            } else {
                imagePanel.setRuler(e.getPoint());
                double dx = x - mark.x;
                double dy = y - mark.y;
                double dd = Math.sqrt(dx*dx + dy*dy);
                double dp = dd / map.pixelUnit();
                while (true) {
                    String text = JOptionPane.showInputDialog(frame, 
                            new String[] { dd + " pixels", dp + " KM", "Enter new distance in KM" });
                    if (text == null)
                        break;
                    try {
                        double km = Double.parseDouble(text);
                        map.setPixelUnit(dd / km);
                        break;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "NumberFormatException", JOptionPane.ERROR_MESSAGE);
                    }
                }
                imagePanel.setMark(null);
                imagePanel.setRuler(null);
            }
        }
        if (nodes.isSelected()) {
            Node node = searchNode(x, y);
            if (node == null) {
                if (nodeDialog.showNew("NEW node at (" + x + "," + y + ")", x, y)) {
                    String name = nodeDialog.nodeName();
                    NodeType type = nodeDialog.nodeType();
                    x = nodeDialog.coordX();
                    y = nodeDialog.coordY();
                    node = type.createNode(name, x, y);
                    node.askDetail(frame);
                    map.addNode(node);
                    imagePanel.repaint();
                }
            } else {
                nodeDialog.setNodeName(node.getName());
                nodeDialog.setNodeType(node.type());
                nodeDialog.showNode("EDIT node at (" + x + "," + y + ")", node.x(), node.y());
                x = nodeDialog.coordX();
                y = nodeDialog.coordY();
                if (x != node.x() || y != node.y()) {
                    node.setCoord(x, y);
                }
                node.askDetail(frame);
                imagePanel.repaint();
            }
        }
        if (streets.isSelected()) {
            final Point mark = imagePanel.mark();
            if (mark != null) {
                Node last = searchNode(mark.x, mark.y);
                if (last != null && last.canConnect(mark.x, mark.y)) {
                    Node node = searchNode(x, y);
                    if (node != null && node.canConnect(x, y) && node != last) {
                        last.connect(node, mark.x, mark.y);
                        node.connect(last, x, y);
                        imagePanel.repaint();
                    }
                }
                imagePanel.setMark(null);
            } else {
                Node node = searchNode(x, y);
                if (node != null) {
                    switch (node.getDirection(x, y)) {
                        case NORTH:
                            x = node.x();
                            y = node.y() - ImagePanel.STREET_DELTA;
                            break;
                        case EAST:
                            x = node.x() + ImagePanel.STREET_DELTA;
                            y = node.y();
                            break;
                        case SOUTH:
                            x = node.x();
                            y = node.y() + ImagePanel.STREET_DELTA;
                            break;
                        case WEST:
                            x = node.x() - ImagePanel.STREET_DELTA;
                            y = node.y();
                            break;
                        default:
                            break;
                    }
                }
                imagePanel.setMark(new Point(x, y));
            }
        }
    }

    private void onMouseClickedCtrl(MouseEvent e) {
        if (map == null) return;
        int x = e.getX();
        int y = e.getY();
        if (nodes.isSelected()) {
            Node node = searchNode(x, y);
            if (node != null) {
                map.removeNode(node);
                imagePanel.repaint();
            }
        } else if (streets.isSelected()) {
            if (imagePanel.mark() != null) {
                imagePanel.setMark(null);
            } else {
                Node node = searchNode(x, y);
                if (node != null) {
                    Node other = node.disconnect(x, y);
                    if (other != null) {
                        other.removeNode(node);
                    }
                    imagePanel.repaint();
                }
            }
        }
    }

    private Node searchNode(int x, int y) {
        if (map == null) return null;
        Node result = null;
        double dist = 0;
        for (Node node : map.nodes()) {
            double dx = node.x() - x;
            double dy = node.y() - y;
            double d = Math.sqrt(dx*dx + dy*dy);
            if (d <= RANGE) {
                if (result == null || d < dist) {
                    result = node;
                    dist = d;
                }
            }
        }
        return result;
    }
    
    private JButton newJButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        return button;
    }
    
    private JToggleButton newJToggleButton(String text) {
        JToggleButton button = new JToggleButton(text);
        return button;
    }
}
