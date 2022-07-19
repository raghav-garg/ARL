import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowLog extends RateLimiter{

    //Though in above implementation we use a lock while performing operations on the log, in practice,
    //Redis’s sorted set and ZREMRANGEBYSCORE command can provide atomic operations to accomplish this

    //pros : it provides a more accurate rate limit because the window boundary is dynamic instead of fixed. [t-1,t]
    //cons : its memory footprint. We notice that, even if a request is rejected, its request time is recorded in the log, making the log unbounded.

    private final Queue<Long> log = new LinkedList<>();

    protected SlidingWindowLog(int maxRequestPerSec) {
        super(maxRequestPerSec);
    }

    @Override
    boolean allow() {
        long curTime = System.currentTimeMillis();
        long boundary = curTime - 1000;
        synchronized (log) {
            while (!log.isEmpty() && log.element() <= boundary) {
                log.poll();
            }
            log.add(curTime);
            System.out.println(curTime + ", log size = " + log.size() + (log.size() <= maxRequestPerSec));
            return log.size() <= maxRequestPerSec;
    }
    }
}
