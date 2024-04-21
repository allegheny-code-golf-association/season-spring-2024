package cfh;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.1, 19.01.2010
 * @author Carlos Heuberger <br/>
 *         <a href="https://CHeuberger.github.io/">https://CHeuberger.github.io/</a> <br/>
 *         <a href="http://simu.wikidot.com/">http://simu.wikidot.com/</a>
 */
public class Timer {

    private final ThreadMXBean threadMX = ManagementFactory.getThreadMXBean();
    private final long elapsedStart;
    private final long cpuStart;
    private final long userStart;

    
    public Timer() {
        elapsedStart = System.nanoTime();
        cpuStart = threadMX.getCurrentThreadCpuTime();
        userStart = threadMX.getCurrentThreadUserTime();
    }

    public Times times() {
        long e = System.nanoTime();
        long c = threadMX.getCurrentThreadCpuTime();
        long u = threadMX.getCurrentThreadUserTime();
        return new Times(e - elapsedStart, c - cpuStart, u - userStart);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Times {

        private final long elapsed;
        private final long cpu;
        private final long user;

        
        public Times(long elapsed, long cpu, long user) {
            this.elapsed = elapsed;
            this.cpu = cpu;
            this.user = user;
        }

        public long elapsed() {
            return elapsed;
        }

        public double elapsedSec() {
            return elapsed / 1.0e9;
        }

        public long cpu() {
            return cpu;
        }

        public double cpuSec() {
            return cpu / 1.0e9;
        }

        public long user() {
            return user;
        }

        public double userSec() {
            return user / 1.0e9;
        }

        public Times add(Times other) {
            return new Times(elapsed+other.elapsed, cpu+other.cpu, user+other.user);
        }

        public Times subtract(Times other) {
            return new Times(elapsed-other.elapsed, cpu-other.cpu, user-other.user);
        }

        @Override
        public String toString() {
            return String.format("elapsed=%-8.3f cpu=%-8.3f user=%-8.3f [seconds]", 
                    elapsedSec(), cpuSec(), userSec());
        }

        public String toString(TimeUnit unit) {
            double factor = TimeUnit.NANOSECONDS.convert(1, unit);
            return String.format("elapsed=%-8.3f cpu=%-8.3f user=%-8.3f [%s]", 
                    elapsed / factor, cpu / factor, user / factor, unit);
        }
    }
}
