package seatsio.util;

/**
 * Time source and sleep for retries, so that tests can replace them.
 */
interface RetryClock {

    RetryClock SYSTEM = new RetryClock() {
        @Override
        public long millis() {
            return System.nanoTime() / 1_000_000;
        }

        @Override
        public void sleep(long millis) throws InterruptedException {
            Thread.sleep(millis);
        }
    };

    long millis();

    void sleep(long millis) throws InterruptedException;
}
