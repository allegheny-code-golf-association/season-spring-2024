package cfh.taxi;

public enum Direction {

    NORTH,
    EAST,
    SOUTH,
    WEST;
    
    public Direction left() {
        int i = ordinal() + 3;  // -1 + 4 (avoid negative)
        return values()[i % 4];
    }
    
    public Direction right() {
        int i = ordinal() + 1;
        return values()[i % 4];
    }
    
    public Direction opposite() {
        int i = ordinal() + 2;
        return values()[i % 4];
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static Direction parse(String name) {
        switch (name) {
            case "N": return NORTH;
            case "E": return EAST;
            case "S": return SOUTH;
            case "W": return WEST;
            default:  return valueOf(name);
        }
    }
}
