public class Main {

    public static void main(String[] args) {

        RateLimiter rateLimiter = new FixedWindow(3);
        ExecuteTestsForFixedWindow();
        //ExecuteTestsForSlidingWindowLog();
        //ExecuteTestsForSlidingWindow();
    }

    public static void ExecuteTestsForFixedWindow()
    {
        int MAX_REQUEST_PER_SEC = 3;
        RateLimiter rateLimiter = new FixedWindow(MAX_REQUEST_PER_SEC);
        sendRequest(rateLimiter,10);
    }

    public static void ExecuteTestsForSlidingWindowLog()
    {
        int MAX_REQUEST_PER_SEC = 15;
        RateLimiter rateLimiter = new FixedWindow(MAX_REQUEST_PER_SEC);
        sendRequest(rateLimiter,16);
    }


    public static void ExecuteTestsForSlidingWindow()
    {
        int MAX_REQUEST_PER_SEC = 15;
        RateLimiter rateLimiter = new FixedWindow(MAX_REQUEST_PER_SEC);
        sendRequest(rateLimiter,16);
    }


    private static void sendRequest(RateLimiter rateLimiter, int totalCnt)
    {
        for (int i = 0; i < totalCnt; i++) {
            new Thread(() -> System.out.println(rateLimiter.allow())).start();
        }
    }

}
