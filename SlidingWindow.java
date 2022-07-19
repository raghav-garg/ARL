import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SlidingWindow extends RateLimiter {

    // TODO: Clean up stale entries

    //This is still not accurate becasue it assumes that the distribution of requests in previous window is even,
    // which may not be true. But compares to fixed window counter, which only guarantees rate within each window,
    // and sliding window log, which has huge memory footprint, sliding window is more practical.


    //Sliding window counter is similar to fixed window counter but it smooths out bursts of traffic by adding a weighted count in previous window to the count in current window.
    //For example, suppose the limit is 10 per minute. There are 9 requests in window [00:00, 00:01) and 5 reqeuests in window [00:01, 00:02).
    //For a requst arrives at 00:01:15, which is at 25% position of window [00:01, 00:02),
    // we calculate the request count by the formula: 9 x (1 - 25%) + 5 = 11.75 > 10.
    // Thus we reject this request. Even though both windows don’t exceed the limit,
    // the request is rejected because the weighted sum of previous and current window does exceed the limit.

    private final ConcurrentMap<Long, AtomicInteger> windows = new ConcurrentHashMap<>();

    protected SlidingWindow(int maxRequestPerSec) {
        super(maxRequestPerSec);
    }

    @Override
    boolean allow() {
        long curTime = System.currentTimeMillis();
        long curWindowKey = curTime / 1000 * 1000;
        windows.putIfAbsent(curWindowKey, new AtomicInteger(0));
        long preWindowKey = curWindowKey - 1000;
        AtomicInteger preCount = windows.get(preWindowKey);
        if (preCount == null) {
            return windows.get(curWindowKey).incrementAndGet() <= maxRequestPerSec;
        }

        double preWeight = 1 - (curTime - curWindowKey) / 1000.0;
        long count = (long) (preCount.get() * preWeight
                + windows.get(curWindowKey).incrementAndGet());
        return count <= maxRequestPerSec;
    }
}
