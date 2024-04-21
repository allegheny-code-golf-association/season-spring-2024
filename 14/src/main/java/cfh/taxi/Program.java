package cfh.taxi;

import static cfh.taxi.Program.InputOutput.Level.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cfh.Timer;
import cfh.Timer.Times;
import cfh.taxi.cmd.Label;

public class Program {

    public interface InputOutput {
        enum Level { 
            PRINT("log minimal information"), 
            INFO("log some information"), 
            DEBUG("log debug information");
            
            private final String tooltip;
            
            private Level(String tooltip) {
                this.tooltip = tooltip;
            }
            
            public String tooltip() {
                return tooltip;
            }
        }

        boolean isLogging(Level level);
        void log(Level level, String format, Object... args);

        String readLine() throws TaxiException;
        void print(String format, Object... args);
        void error(String format, Object... args);
    }

    //----------------------------------------------------------------------------------------------

    public interface Result {
        boolean isEnd();
        String nextLabel();
        String message();
    }
    
    //----------------------------------------------------------------------------------------------

    public static class JumpLabel implements Result {
        private final String label;
        
        public JumpLabel(String label) {
            if (label == null) throw new IllegalArgumentException("null lavel");
            this.label = label;
        }
        @Override
        public boolean isEnd() {
            return false;
        }
        @Override
        public String nextLabel() {
            return label;
        }
        @Override
        public String message() {
            return toString();
        }
        @Override
        public String toString() {
            return "Jump to " + label;
        }
    }
    
    //----------------------------------------------------------------------------------------------

    public static class TheEnd implements Result {
        private final String message;
        
        public TheEnd(String msg) {
            message = msg;
        }
        @Override
        public boolean isEnd() {
            return true;
        }
        @Override
        public String nextLabel() {
            return null;
        }
        @Override
        public String message() {
            return message;
        }
        @Override
        public String toString() {
            return "THE END - " + message;
        }
    }

    //==============================================================================================

    private final RoadMap map;
    private final InputOutput inpout;

    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Integer> labels = new HashMap<>();

    private final List<Listener> listeners = new ArrayList<>();
    
    
    public Program(RoadMap map, InputOutput inpout, String text) throws ParseException {
        if (map == null) throw new IllegalArgumentException("null map");
        if (inpout == null) throw new IllegalArgumentException("null output");
        if (text == null) throw new IllegalArgumentException("null text");

        this.map = map;
        this.inpout = inpout;
        compile(text);
    }

    private void compile(String text) throws ParseException {
        assert text != null;
        
        inpout.log(INFO, "compiling %d characters", text.length());
        Timer timer = new Timer();
        Parser parser = new Parser(map, text);
        while (parser.hasMore()) {
            Command cmd = parser.nextCommand();
            if (cmd == null)
                continue;
            commands.add(cmd);
            inpout.log(DEBUG, "  %d: %s", cmd.line, cmd);
            if (cmd instanceof Label) {
                Label label = (Label) cmd;
                Integer old = labels.get(label.name());
                if (old != null) {
                    int oldline = commands.get(old).line;
                    inpout.log(null, "WARNING: %d:label %s already defined at line %d", cmd.line, label, oldline);
                }
                labels.put(label.name(), commands.size()-1);
            }
            inpout.log(DEBUG, "  %3d: %s", cmd.line, cmd);
        }
        Times times = timer.times();
        inpout.log(INFO, "%d commands compiled successfully", commands.size());
        inpout.log(INFO, "%s", times.toString());
    }

    public void run() throws TaxiException {
        inpout.print("Welcome to Taxi!                                                %n");
        inpout.print("Let the journey begin...%n");
        inpout.print("%n");
        inpout.log(PRINT, "%n%nStarting...");
        map.reset();
        Taxi taxi = new Taxi(map, inpout);
        for (Listener listener : listeners) {
            taxi.addListener(listener);
            listener.startProgram(taxi);
        }
        
        int executedCount = 0;
        boolean backInGarage = false;
        Timer timer = new Timer();
        try {
            for (int pc = 0; pc < commands.size() && !Thread.currentThread().isInterrupted(); pc++) {
                Command cmd = commands.get(pc);
                if (cmd == null)
                    continue;
                if (!(cmd instanceof Label)) {
                    inpout.log(DEBUG, "  gas: %.1f, cred: %.2f, dist: %.1f, %s, %d:%s", 
                        taxi.tank(), taxi.cash()/100.0, taxi.distance(), taxi.location(), cmd.line, cmd);
                }
                Result result = cmd.execute(taxi, inpout);
                executedCount += 1;
                if (result == null) {
                    // nothing
                } else if (result.isEnd()) {
                    inpout.print("%n%n%s%nProgram complete.%n%n", result.message());
                    inpout.log(DEBUG, "  back home, lets call it a day");
                    backInGarage = true;
                    break;
                } else {
                    String name = result.nextLabel();
                    Integer jump = labels.get(name);
                    if (jump == null)
                        throw new TaxiException("no such label [" + name + "]");
                    pc = jump;
                    inpout.log(DEBUG, "  switching to \"%s\"(%d)", name, jump);
                }
            }
        } catch (TaxiException ex) {
            for (Listener listener : listeners) {
                listener.taxiException(ex);
            }
            throw ex;
        } finally {
            for (Listener listener : listeners) {
                listener.endProgram(taxi);
                taxi.removeListener(listener);
            }
        }
        Times times = timer.times();
        if (Thread.interrupted()) {  // must be cleared else Error in one of the following inpout's
            inpout.error("INTERRUPTED by user!");
            inpout.log(null, "%nINTERRUPTED by user!%n");
        } else {
            inpout.log(PRINT, "THE END!");
            if (!backInGarage) {
                inpout.error("%nThe boss couldn't find your taxi in the garage.  You're fired!%n%n");
            }
        }
        inpout.log(null, "%.2f credits", taxi.cash() / 100.0);
        inpout.log(null, "%.1f gas left in tank", taxi.tank());
        inpout.log(null, "%.1f units travelled", taxi.distance());
        List<Passenger> list = taxi.passengers();
        if (!list.isEmpty()) {
            inpout.log(PRINT, "%d passengers in taxy", list.size());
            if (inpout.isLogging(INFO)) {
                for (Passenger passenger : list) {
                    inpout.log(INFO, "  %s going to %s", passenger, passenger.destination());
                }
            }
        }
        int cnt = map.waitingCount();
        if (cnt > 0) {
            inpout.log(PRINT, "%d passengers waiting", cnt);
            if (inpout.isLogging(INFO)) {
                for (Node node : map.nodes()) {
                    cnt = node.waitingCount();
                    if (cnt > 0) {
                        inpout.log(INFO, "  %d at %s", cnt, node.getName());
                        if (inpout.isLogging(DEBUG)) {
                            for (Passenger passenger : node.waiting()) {
                                inpout.log(DEBUG, "    %s", passenger);
                            }
                        }
                    }
                }
            }
        }
        inpout.log(INFO, "%d commands executed", executedCount);
        inpout.log(INFO, "%d passengers transported", taxi.paxCount());
        inpout.log(PRINT, "%s", times.toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Command command : commands) {
            builder.append(command).append('\n');
        }
        return builder.toString();
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static interface Listener extends Taxi.Listener {

        void startProgram(Taxi taxi);

        void taxiException(TaxiException ex);

        void endProgram(Taxi taxi);
    }
}
