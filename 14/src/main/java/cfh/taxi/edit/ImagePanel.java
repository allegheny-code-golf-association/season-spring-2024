package cfh.taxi.edit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

import cfh.taxi.Direction;
import cfh.taxi.Node;
import cfh.taxi.RoadMap;

class ImagePanel extends JPanel {
    
    public static final int STREET_DELTA = 8;
    
    private static final Color NODE_COLOR = Color.RED;
    private static final int NODE_SIZE = 9;
    private static final Color START_COLOR = Color.YELLOW;
    private static final Color STREET_COLOR = Color.BLUE;
    private static final Color MARK_COLOR = Color.BLUE;
    private static final Color RULER_COLOR = Color.GREEN.darker();
    
    private RoadMap map = null;
    private Point mark = null;
    private Point ruler = null;
    
    ImagePanel() {
    }

    void setRoadMap(RoadMap map) {
        this.map = map;
        this.mark = null;
        if (map != null) {
            Image image = map.image();
            setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
        }
        revalidate();
        repaint();
    }
    
    void setMark(Point point) {
        mark = point;
        repaint();
    }
    
    Point mark() {
        return mark;
    }
    
    void setRuler(Point point) {
        ruler = point;
        repaint();
    }
    
    Point ruler() {
        return ruler;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map != null) {
            g.drawImage(map.image(), 0, 0, this);
            g.setColor(STREET_COLOR);
            for (Node node : map.nodes()) {
                Node to;
                to = node.node(Direction.NORTH);
                int x = node.x();
                int y = node.y();
                if (to != null) {
                    g.drawLine(x, y-STREET_DELTA, (x+to.x())/2, (y+to.y())/2);
                }
                to = node.node(Direction.EAST);
                if (to != null) {
                    g.drawLine(x+STREET_DELTA, y, (x+to.x())/2, (y+to.y())/2);
                }
                to = node.node(Direction.SOUTH);
                if (to != null) {
                    g.drawLine(x, y+STREET_DELTA, (x+to.x())/2, (y+to.y())/2);
                }
                to = node.node(Direction.WEST);
                if (to != null) {
                    g.drawLine(x-STREET_DELTA, y, (x+to.x())/2, (y+to.y())/2);
                }
            }
            g.setColor(NODE_COLOR);
            for (Node node : map.nodes()) {
                int nx = node.x();
                int ny = node.y();
                g.drawLine(nx-NODE_SIZE, ny, nx+NODE_SIZE, ny);
                g.drawLine(nx, ny-NODE_SIZE, nx, ny+NODE_SIZE);
                g.drawString(node.getName(), nx+4, ny+22);
            }
            Node start = map.start();
            if (start != null) {
                g.setColor(START_COLOR);
                int x = start.x();
                int y = start.y();
                g.drawLine(x, y - NODE_SIZE/2 + 1, x + NODE_SIZE/2 - 1, y + NODE_SIZE/3 - 1);
                g.drawLine(x + NODE_SIZE/2 - 1, y + NODE_SIZE/3 - 1, x - NODE_SIZE/2 + 1, y + NODE_SIZE/3 - 1);
                g.drawLine(x - NODE_SIZE/2 + 1, y + NODE_SIZE/3 - 1, x, y - NODE_SIZE/2 + 1);
                
                g.drawLine(x, y - NODE_SIZE/2, x + NODE_SIZE/2, y + NODE_SIZE/3);
                g.drawLine(x + NODE_SIZE/2, y + NODE_SIZE/3, x - NODE_SIZE/2, y + NODE_SIZE/3);
                g.drawLine(x - NODE_SIZE/2, y + NODE_SIZE/3, x, y - NODE_SIZE/2);
                
                g.drawLine(x, y - NODE_SIZE/2 - 1, x + NODE_SIZE/2 + 1, y + NODE_SIZE/3 + 1);
                g.drawLine(x + NODE_SIZE/2 + 1, y + NODE_SIZE/3 + 1, x - NODE_SIZE/2 - 1, y + NODE_SIZE/3 + 1);
                g.drawLine(x - NODE_SIZE/2 - 1, y + NODE_SIZE/3 + 1, x, y - NODE_SIZE/2 - 1);
            }
            if (mark != null) {
                int size;
                g.setColor(MARK_COLOR);
                size = NODE_SIZE;
                g.drawOval(mark.x-size, mark.y-size, 2*size+1, 2*size+1);
                size /= 2;
                g.drawOval(mark.x-size, mark.y-size, 2*size+1, 2*size+1);
            }
            if (ruler != null) {
                int size;
                g.setColor(MARK_COLOR);
                size = NODE_SIZE;
                g.drawOval(ruler.x-size, ruler.y-size, 2*size-1, 2*size-1);
                size /= 2;
                g.drawOval(ruler.x-size, ruler.y-size, 2*size-1, 2*size-1);
                if (mark != null) {
                    g.setColor(RULER_COLOR);
                    g.drawLine(mark.x, mark.y, ruler.x, ruler.y);
                }
            }
        }
    }
}
