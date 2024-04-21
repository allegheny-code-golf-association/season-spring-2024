package cfh.taxi.gui;

public class Animation {
    
    private boolean enabled = false;
    private double velocity = 1.0;

    Animation() {
    }
    
    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    boolean isEnabled() {
        return enabled;
    }
    
    void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
    double velocity() {
        return velocity;
    }

    double delta() {
        return 0.1 * velocity;
    }

    long timeDelta() {
        return 50;
    }

    long paxDisplayDelta() {
        return (long) (50 / velocity);
    }
}
