package cfh.taxi.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;

import cfh.taxi.Direction;
import cfh.taxi.Location;
import cfh.taxi.Node;
import cfh.taxi.RoadMap;
import cfh.taxi.node.Corner;
import cfh.taxi.node.Intersection;
import cfh.taxi.node.Rotatory;

public class MapPanel extends JPanel {

    private final RoadMap map;
    private Animation animation;
    private final Car car;
    
    private boolean showNames = false;
    private boolean showRoads = false;
    private List<Node> route = null;
    private int routeAlpha = 0;
    private double scale = 1.0;
    
    private Node destination = null;

    MapPanel(RoadMap map) {
        if (map == null) throw new IllegalArgumentException("null map");
        
        this.map = map;
        animation = new Animation();
        car = new Car(this, map, animation);
        
        setFont(new Font("monospaced", Font.PLAIN, 12));
        setLayout(null);
    }
    
    void clear() {
        car.clear();
    }
    
    void setShowNames(boolean b) {
        if (showNames != b) {
            showNames = b;
            repaint();
        }
    }
    
    void setShowRoads(boolean b) {
        if (showRoads != b) {
            showRoads = b;
            repaint();
        }
    }

    void enableAnimation(boolean enable) {
        if (enable != animation.isEnabled()) {
            animation.setEnabled(enable);
            repaint();
        }
    }
    
    void setAnimationVelocity(double velocity) {
        if (velocity != animation.velocity()) {
            animation.setVelocity(velocity);
            repaint();
        }
    }

    void setRoute(List<Node> nodes) {
        route = nodes;
        routeAlpha = 255;
        repaint();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(700);
                    for (routeAlpha = 255; routeAlpha > 0; routeAlpha -= 25) {
                        repaint();
                        Thread.sleep(50);
                    }
                } catch (InterruptedException ignored) {
                }
                routeAlpha = 0;
                route = null;
                repaint();
            }
        }).start();
    }

    void setScale(double d) {
        if (d <= 0) throw new IllegalArgumentException("scale not positive: " + d);
        
        scale = d;
        revalidate();
        repaint();
    }
    
    double getScale() {
        return scale;
    }
    
    void setDestination(Node destination) {
        this.destination = destination;
        repaint();
    }
    
    Car getCar() {
        return car;
    }

    @Override
    public Dimension getPreferredSize() {
        if (map != null)
            return new Dimension(
                    (int)Math.round(map.image().getWidth()*scale) + 100, 
                    (int)Math.round(map.image().getHeight()*scale));
        else
            return super.getPreferredSize();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gg = (Graphics2D) g;
        gg.addRenderingHints(Collections.singletonMap(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        gg.scale(scale, scale);
       
        if (map != null) {
            gg.drawImage(map.image(), 0, 0, this);
            
            if (showRoads) {
                gg.setColor(new Color(0, 0, 255, 63));
                Stroke tmp = gg.getStroke();
                gg.setStroke(new BasicStroke(7));
                try {
                    for (Node from : map.nodes()) {
                        for (Direction dir : Direction.values()) {
                            Node to = from.node(dir);
                            if (to != null) {
                                gg.drawLine(from.x(), from.y(), to.x(), to.y());
                            }
                        }
                    }
                } finally {
                    gg.setStroke(tmp);
                }
            }
            
            if (showNames) {
                Font tmp = gg.getFont();
                try {
                    gg.setFont(new Font("Arial", Font.PLAIN, 18));
                    for (Node node : map.nodes()) {
                        if (node instanceof Corner) {
                        } else if (node instanceof Intersection) {
                            paintNode(gg, node, Color.BLACK);
                        } else if (node instanceof Rotatory) {
                            paintNode(gg, node, Color.BLUE);
                        } else if (node instanceof Location) {
                            paintNode(gg, node, Color.RED);
                            gg.drawString(node.getName(), node.x()+12, node.y()+22);
                        } else {
                            System.err.println("unhandled node " + node);
                        }
                    }
                } finally {
                    gg.setFont(tmp);
                }
            }
            
            for (Node node : map.nodes()) {
                int count = node.waitingCount();
                if (count > 0) {
                    gg.drawString(Integer.toString(count), node.x()+4, node.y()+20);
                }
            }
            
            if (route != null) {
                gg.setColor(new Color(0, 0, 255, routeAlpha));
                Stroke tmp = gg.getStroke();
                gg.setStroke(new BasicStroke(5));
                try {
                    Node last = null;
                    for (Node node : route) {
                        if (last != null) {
                            gg.drawLine(last.x(), last.y(), node.x(), node.y());
                        }
                        last = node;
                    }
                } finally {
                    gg.setStroke(tmp);
                }
            }
            
            if (destination != null) {
                int x = destination.x();
                int y = destination.y();
                gg.setColor(Color.GREEN);
                Polygon pol = new Polygon(new int[] { x+20, x+40, x+10 }, new int[] { y-40, y-30, y-20 }, 3);
                gg.fillPolygon(pol);
                gg.setColor(Color.BLACK);
                gg.drawPolygon(pol);
                gg.setStroke(new BasicStroke(2));
                gg.drawLine(x, y, x+19, y-38);
            }
        }
        
        if (car != null) {
            car.paint(gg);
        }
    }
    
    private void paintNode(Graphics2D gg, Node node, Color color) {
        final int NODE_SIZE = 11;
        
        Color tr = new Color(color.getRed(), color.getGreen(), color.getBlue(), 64);
        gg.setColor(tr);
        gg.fillOval(node.x()-NODE_SIZE /2, node.y()-NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
        gg.setColor(color);
        gg.fillOval(node.x()-NODE_SIZE/2+2, node.y()-NODE_SIZE/2+2, NODE_SIZE-4, NODE_SIZE-4);
    }
}
