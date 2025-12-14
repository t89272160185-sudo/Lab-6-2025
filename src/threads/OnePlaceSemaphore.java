package threads;

/**
 * A single-slot semaphore differentiating read and write phases.
 */
public class OnePlaceSemaphore {
    private boolean hasValue;

    public synchronized void beginWrite() throws InterruptedException {
        while (hasValue) {
            wait();
        }
    }

    public synchronized void endWrite() {
        hasValue = true;
        notifyAll();
    }

    public synchronized void beginRead() throws InterruptedException {
        while (!hasValue) {
            wait();
        }
    }

    public synchronized void endRead() {
        hasValue = false;
        notifyAll();
    }
}
