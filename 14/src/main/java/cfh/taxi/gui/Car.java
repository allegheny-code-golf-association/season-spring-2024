package cfh.taxi.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;

import cfh.taxi.Direction;
import cfh.taxi.Location;
import cfh.taxi.Node;
import cfh.taxi.Passenger;
import cfh.taxi.Program;
import cfh.taxi.RoadMap;
import cfh.taxi.Taxi;
import cfh.taxi.TaxiException;

public class Car implements Program.Listener {
    
    private final MapPanel parent;
    private final RoadMap map;
    private final Animation animation;
    
    private int fade;
    private int x;
    private int y;
    private double heading;
    private double tank;
    private int occupation;
    private Throwable exception;
    
    private Passenger passenger = null;
    private int passengerDelta;
    
    Car(MapPanel parent, RoadMap map, Animation animation) {
        assert parent != null;
        assert animation != null;
        
        this.parent = parent;
        this.map = map;
        this.animation = animation;
    }

    void paint(Graphics2D gg) {
        if (!animation.isEnabled()) return;
        
        Polygon shape = new Polygon(
            new int[] { 30, -15, -10, -15,  30}, 
            new int[] {  0,  15,   0, -15,   0}, 
            5);
        Graphics2D tmp = (Graphics2D) gg.create();
        try {
            tmp.translate(x, y);

            tmp.scale(1.0/parent.getScale(), 1.0/parent.getScale());
            
            FontMetrics fm = tmp.getFontMetrics();
            if (passenger != null) {
                String str = passenger.toString();
                int tx = -10 + passengerDelta/2;
                int ty = 15 + passengerDelta;
                int tw = fm.stringWidth(str);
                int th = fm.getHeight();
                int alpha = passengerDelta > 12 ? 255 : passengerDelta*255/12;
                tmp.setColor(new Color(192, 192, 192, alpha));
                tmp.fillRect(tx-6, ty-3-fm.getAscent(), tw+10, th+6);
                tmp.setColor(new Color(128, 128, 128, alpha));
                tmp.fillRect(tx-5, ty-2-fm.getAscent(), tw+8, th+4);
                tmp.setColor(new Color(255, 255, 255, alpha));
                tmp.fillRect(tx-4, ty-1-fm.getAscent(), tw+6, th+2);
                tmp.setColor(new Color(0, 0, 255, 127+alpha/2));
                tmp.drawString(str, tx, ty);
            }
            
            if (occupation > 0) {
                tmp.setColor(new Color(0, 0, 0, fade));
                tmp.drawString(Integer.toString(occupation), 15, -10);
            }
            
            tmp.scale(parent.getScale(), parent.getScale());
            
            tmp.rotate(heading);
            Color color1;
            Color color2;
            if (exception == null) {
                color1 = new Color(32, 255, 32, fade);
                color2 = new Color(0, 178, 0, fade);
            } else {
                color1 = new Color(255, 0, 0, fade);
                color2 = new Color(192, 0, 0, fade);
            }
            tmp.setPaint(new GradientPaint(0, -5, color1, 0, 5, color2));
            tmp.fill(shape);
            tmp.rotate(-heading);
            
            if (exception != null) {
                tmp.scale(1.0/parent.getScale(), 1.0/parent.getScale());
                String str = exception.getMessage();
                int tx = -80;
                int ty = 30;
                int tw = fm.stringWidth(str);
                int th = fm.getHeight();
                tmp.setColor(new Color(255, 255, 255, fade));
                tmp.fillRect(tx-6, ty-3-fm.getAscent(), tw+10, th+6);
                tmp.setColor(new Color(192, 0, 0, fade));
                tmp.fillRect(tx-4, ty-1-fm.getAscent(), tw+6, th+2);
                tmp.setColor(new Color(255, 255, 255, 127+fade/2));
                tmp.drawString(str, tx, ty);
            }
        } finally {
            tmp.dispose();
        }
    }

    void clear() {
        try {
            for (; fade > 0; fade -= 17) {
                repaint();
                Thread.sleep(animation.timeDelta());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        fade = 0;
        passenger = null;
        exception = null;
        repaint();
    }
    
    void repaint() {
        if (!animation.isEnabled()) return;
        parent.repaint();
    }
    
    @Override
    public void startProgram(Taxi taxi) {
        clear();
        
        tank = taxi.tank() / map.capacity();
        x = taxi.location().x();
        y = taxi.location().y();
        occupation = taxi.passengers().size();
        try {
            for (fade = 0; fade < 255; fade += 17) {
                repaint();
                Thread.sleep(animation.timeDelta());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        fade = 255;
        repaint();
    }

    @Override
    public void drive(Taxi taxi, Direction dir) {
    }

    @Override
    public void move(Taxi taxi, Node from, Node dest) {
        if (!animation.isEnabled()) {
            tank = taxi.tank() /map.capacity();
            return;
        }
        x = from.x();
        y = from.y();
        int dx = dest.x() - x;
        int dy = dest.y() - y;
        double dist = map.distance(from, dest);
        heading = Math.atan2(dy, dx);
        double delta = animation.delta();
        try {
            if (delta > 0.0) {
                for (double d = 0.0; d < dist; d += delta) {
                    Thread.sleep(animation.timeDelta());
                    tank -= animation.delta() / map.gasUsage() / map.capacity();
                    if (tank >= 0) {
                        x = (int) (from.x() + dx * d / dist);
                        y = (int) (from.y() + dy * d / dist);
                    } else {
                        tank = 0;
                        break;
                    }
                    repaint();
                }
            } else {
                Thread.sleep(animation.timeDelta());                
            }
            tank = taxi.tank() / map.capacity();
            if (tank >= 0) {
                x = dest.x();
                y = dest.y();
            }
            repaint();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void refuel(Taxi taxi, double quant) {
        tank = taxi.tank() / map.capacity();
        if (!animation.isEnabled()) return;
        try {
            Thread.sleep(10 * animation.timeDelta());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void borded(Taxi taxi, Passenger pax) {
        if (!animation.isEnabled()) return;
        showPax(pax, false);
        occupation = taxi.passengers().size();
    }

    @Override
    public void left(Taxi taxi, List<Passenger> left) {
        if (!animation.isEnabled()) return;
        for (Passenger pax : left) {
            if (Thread.currentThread().isInterrupted())
                break;
            occupation -= 1;
            showPax(pax, true);
        }
        occupation = taxi.passengers().size();
        repaint();
    }

    private void showPax(Passenger pax, boolean leaving) {
        passenger = pax;
        passengerDelta = leaving ? 0 : 20;
        repaint();
        try {
            for (int i = 0; i < 20; i++) {
                Thread.sleep(animation.paxDisplayDelta());
                passengerDelta += leaving ? +1 : -1;
                repaint();
            }
            Thread.sleep(animation.timeDelta());
            passenger = null;
            repaint();
            Thread.sleep(2 * animation.timeDelta());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void endProgram(Taxi taxi) {
        tank = taxi.tank() / map.capacity();
        occupation = taxi.passengers().size();
        passenger = null;
        repaint();
    }

    @Override
    public void taxiException(TaxiException ex) {
        exception = ex;
        repaint();
    }
    

    @Override
    public void deadEnd(Taxi taxi, Direction dir) {
    }

    @Override
    public void lost(Taxi taxi, Location destination) {
    }

    @Override
    public void outOfFuel(Taxi taxi) {
    }

    @Override
    public void cash(Taxi taxi, long cash) {
    }
}
