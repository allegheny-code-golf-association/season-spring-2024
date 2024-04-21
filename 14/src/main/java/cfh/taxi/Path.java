package cfh.taxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {

    public static enum Turn {
        LEFT, RIGHT;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    private final Direction start;
    private final List<Instruction> instructions = new ArrayList<>();
    
    public Path(Direction start) {
        if (start == null) throw new IllegalArgumentException("null start");
        
        this.start = start;
    }
    
    void addLeftInstruction(int count) {
        if (count < 1) throw new IllegalArgumentException("count not positive: " + count);
        
        instructions.add(new Instruction(count, Turn.LEFT));
    }
    
    void addRightInstruction(int count) {
        if (count < 1) throw new IllegalArgumentException("count not positive: " + count);
        
        instructions.add(new Instruction(count, Turn.RIGHT));
    }
    
    void addDeadEnd() {
        instructions.add(null);
    }
    
    public Direction start() {
        return start;
    }
    
    public List<Instruction> instructions() {
        return Collections.unmodifiableList(instructions);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(start);
        String separator = " ";
        for (Instruction instruction : instructions) {
            builder.append(separator);
            builder.append(instruction);
            separator = ", ";
        }
        return builder.toString();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    
    public static class Instruction {
        private final int count;
        private final Turn turn;
        private Instruction(int count, Turn turn) {
            if (count < 1) throw new IllegalArgumentException("count not positive: " + count);
            if (turn == null) throw new IllegalArgumentException("null turn");
            
            this.count = count;
            this.turn = turn;
        }
        
        public int count() {
            return count;
        }
        
        public Turn turn() {
            return turn;
        }
        
        public boolean isTurnLeft() {
            return turn == Turn.LEFT;
        }
        
        public boolean isTurnRight() {
            return turn == Turn.RIGHT;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(count);
            if (11 <= count && count <= 13) {
                builder.append("th ");
            } else {
                switch (count % 10) {
                    case 1: builder.append("st "); break;
                    case 2: builder.append("nd "); break;
                    case 3: builder.append("rd "); break;
                    default: builder.append("th "); break;
                }
            }
            builder.append(turn);
            return builder.toString();
        }
    }
}
