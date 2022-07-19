import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindow extends RateLimiter{

    // TODO: Clean up stale entries, We can run a job to clean stale windows regularly.
    // For instance, schedule a task running at 00:00:00 to remove all the entries created in previous day.

    //pros : simple implementation
    //cons : boundary cases will be missing

    private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();

    protected FixedWindow(int maxRequestPerSec) {
        super(maxRequestPerSec);
    }

    @Override
    boolean allow() {
        long windowKey = System.currentTimeMillis() / 1000 * 1000;
        windows.putIfAbsent(windowKey, new AtomicInteger(0));
        return windows.get(windowKey).incrementAndGet() <= maxRequestPerSec;
    }
}
