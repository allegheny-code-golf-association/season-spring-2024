package cfh.taxi;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cfh.taxi.cmd.GoToCommand;
import cfh.taxi.cmd.Label;
import cfh.taxi.cmd.Pickup;
import cfh.taxi.cmd.SwitchTo;
import cfh.taxi.cmd.Waiting;

public class Parser {

    private final RoadMap map;
    private final String[] lines;
    private int cursor = 0;
    
    Parser(RoadMap map, String text) {
        if (map == null) throw new IllegalArgumentException("null map");
        if (text == null) throw new IllegalArgumentException("null text");
        
        this.map = map;
        this.lines = text.split("\\n");
    }

    boolean hasMore() {
        return cursor < lines.length;
    }
    
    // "Hello, World!" is waiting at [the] Writer's Depot. 
    // Go to [the] Writer: east 2nd left, 1st right.
    // Pickup a[nother] passenger going to [the] Post Office.
    // [loop]
    // Switch to plan "loop".
    // Switch to plan "loop" if no one is waiting.
    
    // passanger := '"' textWithEscapes '"' | number
    // location := text | 'the ' text
    
    // '[' label ']'
    // 'Switch to plan "' label '"' [' if no one is waiting'] '.'
    // 'Pickup a' ['nother'] ' passenger going to ' location '.'
    // 'Go to ' location ': ' dir ( count ('st ' | 'nd ' | 'rd' | 'th ') ('left' | 'right') ',' )... '.'
    // passanger ' is waiting at ' location '.'
    Command nextCommand() throws ParseException {
        if (!hasMore())
            return null;
        
        String text = lines[cursor].trim();
        cursor += 1;
        
        if (text.isEmpty())
            return null;
        
        Pattern pattern;
        Matcher  matcher;
        
        pattern = Pattern.compile("\\[(.+)\\]");
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return new Label(cursor, map, matcher.group(1));
        }
        
        pattern = Pattern.compile("Switch +to +plan +\"(.+)\"( +if +no +one +is +waiting)?\\.");
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String name = matcher.group(1);
            boolean conditional = matcher.group(2) != null;
            return new SwitchTo(cursor, map, name, conditional);
        }

        pattern = Pattern.compile("Pickup +a(?:nother)? +passenger +going +to +(?:the +)?([^:.,]+)\\.");
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String group = matcher.group(1);
            Location location = map.location(group);
            if (location == null) throw new ParseException(cursor + ": unknown location \"" + group + "\"", cursor);
            return new Pickup(cursor, map, location);
        }

        pattern = Pattern.compile("Go +to +(?:the +)?([^:.,]+): +" +
        		"(?i)(n|north|e|east|s|south|w|west)" +
        		"(?:,? +([^.]+))?\\.");
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            String group;
            group = matcher.group(1);
            Location location = map.location(group);
            if (location == null) 
                throw new ParseException(cursor + ": unknown location \"" + group + "\"", cursor);
            group = matcher.group(2);
            Path path = new Path(Direction.parse(group.toUpperCase()));
            group = matcher.group(3);
            if (group != null) {
                String[] instructions = group.split(",", -1);
                for (String instruction : instructions) {
                    pattern = Pattern.compile("(\\d+)(?:st|nd|rd|th|)? +(l|left|r|right)");
                    matcher = pattern.matcher(instruction.trim());
                    if (matcher.matches()) {
                        group = matcher.group(1);
                        int count;
                        try {
                            count = Integer.parseInt(group);
                            if (count < 1)
                                throw new NumberFormatException("count must be positive: " + count);
                        } catch (NumberFormatException ex) {
                            throw (ParseException) new ParseException(
                                    cursor + ": invalid number \"" + group + "\"", cursor).initCause(ex);
                        }
                        if (matcher.group(2).charAt(0) == 'l') { 
                            path.addLeftInstruction(count);
                        } else {
                            path.addRightInstruction(count);
                        }
                    } else {
                        throw new ParseException(
                                cursor + ": invalid instruction \"" + instruction + "\"", cursor);
                    }
                }
            }
            return new GoToCommand(cursor, map, location, path);
        }
        
        pattern = Pattern.compile("(\\d+(?:\\.\\d*)?|\\.\\d+|\".*\"|'.*') +is +waiting +at +(?:the +)?([^:.,]+)\\.");
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            Value passenger = createPassenger(matcher.group(1));
            String group = matcher.group(2);
            Location location = map.location(group);
            if (location == null) 
                throw new ParseException(cursor + ": unknown location \"" + group + "\"", cursor);
            return new Waiting(cursor, map, passenger, location);
        }
        
        throw new ParseException(cursor + ": unrecognized command \"" + text + "\"", cursor);
    }

    private Value createPassenger(String value) throws ParseException {
        if (value.charAt(0) == '"' || value.charAt(0) == '\'') {
            assert value.charAt(value.length()-1) == value.charAt(0) : value.charAt(value.length()-1);
            StringBuilder builder = new StringBuilder(value);
            builder.deleteCharAt(0);
            builder.deleteCharAt(builder.length()-1);
            int start = 0;
            int i;
            while ((i = builder.indexOf("\\", start)) != -1) {
                if (i == builder.length()-1)
                    throw new ParseException(cursor + ": incomplete escape sequence in String", cursor);
                switch (builder.charAt(i+1)) {
                    case 'n': builder.replace(i, i+2, "\n"); break;
                    case 'r': builder.replace(i, i+2, "\r"); break;
                    case 't': builder.replace(i, i+2, "\t"); break;
                    // TODO \\uxxxx
                    default: builder.deleteCharAt(i); break;
                }
                start = i + 1;
            }
            return Value.createValue(builder.toString());
        } else {
            try {
                // TODO BigDecimal ?
                double v = Double.parseDouble(value);
                return Value.createValue(v);
            } catch (NumberFormatException ex) {
                throw (ParseException) new ParseException(cursor + ": invalid number \"" + value + "\"", cursor).initCause(ex);
            }
        }
    }
}
