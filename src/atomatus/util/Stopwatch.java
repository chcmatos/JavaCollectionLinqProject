package atomatus.util;

/**
 * Stopwatch to check performance.
 */
public final class Stopwatch {

    private long start;
    private long result;

    /**
     * Start a new stopwatch.
     * @return
     */
    public static Stopwatch startNew() {
        Stopwatch sw = new Stopwatch();
        sw.start();
        return sw;
    }

    private Stopwatch() {
        start   = Long.MIN_VALUE;
        result  = Long.MIN_VALUE;
    }

    private void requireNonStarted() {
        if(start != Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch already be staterd!");
        }
    }

    private void requireNonStoped() {
        if(result != Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch already be stoped!");
        }
    }

    private void requireStarted() {
        if(start == Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch was not initialized!");
        }
    }

    private void requireStoped() {
        if(result == Long.MIN_VALUE) {
            throw new IllegalStateException("Stopwatch was not stoped!");
        }
    }

    /**
     * Start a new performance count.
     */
    public void start() {
        requireNonStarted();
        result = Long.MIN_VALUE;
        start  = System.nanoTime();
    }

    /**
     * Stop and calculate performance count.
     */
    public void stop() {
        long stop = System.nanoTime();
        requireNonStoped();
        requireStarted();
        if(result == Long.MIN_VALUE) {
            result = stop - start;
            start  = Long.MIN_VALUE;
        }
    }

    /**
     * Reset stopwatch.
     */
    public void reset() {
        result  = Long.MIN_VALUE;
        start   = Long.MIN_VALUE;
    }

    /**
     * Restart stopwatch.
     */
    public void restart() {
        result  = Long.MIN_VALUE;
        start   = System.nanoTime();
    }

    public long getElapsedInNano() {
        requireStoped();
        return result;
    }

    public long getElapsedInMillis(){
        return getElapsedInNano() / 1000000;
    }

    public long getElapsedInSec(){
        return getElapsedInMillis() / 1000;
    }
}
