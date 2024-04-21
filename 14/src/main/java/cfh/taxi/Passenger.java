package cfh.taxi;

public class Passenger {

    public final Value value;
    
    private Location destination;
    
    private double distance = 0;
    
    public Passenger(Value value) {
        if (value == null) throw new IllegalArgumentException("null value");
        
        this.value = value;
    }
    
    public Passenger(double value) {
        this(Value.createValue(value));
    }
    
    public Passenger(String value) {
        this(Value.createValue(value));
    }
    
    public Passenger(Passenger passenger) {
        if (passenger == null) throw new IllegalArgumentException("null passenger");
        
        this.value = passenger.value;
    }
    
    public void resetDistance() {
        distance = 0;
    }

    public void distance(double travelled) {
        if (travelled < 0) throw new IllegalArgumentException("negative travelled: " + travelled);
        
        distance += travelled;
    }

    public void setDestination(Location destination) {
        if (destination == null) throw new IllegalArgumentException("null destination");
        
        this.destination = destination;
    }
    
    public Location destination() {
        return destination;
    }
    
    public void payFare(Taxi taxi) {
        taxi.receiveFare(this, distance);
        distance = 0;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
