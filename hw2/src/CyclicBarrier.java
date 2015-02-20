import java.util.concurrent.Semaphore;

/**
 * Created by Matt on 2/17/2015.
 */
public class CyclicBarrier {
    Semaphore barrier;
    Semaphore mutex;

    public CyclicBarrier(int parties) {
// Creates a new CyclicBarrier that will trip when
// the given number of parties (threads) are waiting upon it
        barrier = new Semaphore(parties);
        mutex = new Semaphore(1);
    }

    int await() throws InterruptedException {
// Waits until all parties have invoked await on this barrier.
// If the current thread is not the last to arrive then it is
// disabled for thread scheduling purposes and lies dormant until
// the last thread arrives.
// Returns: the arrival index of the current thread, where index
// (parties - 1) indicates the first to arrive and zero indicates
// the last to arrive.
        int myTotal = 0;

        mutex.acquire();
        barrier.acquire();
        myTotal = barrier.availablePermits();
        mutex.release();
        if (myTotal == 0)
            barrier.notifyAll();
        else
            barrier.wait();

        barrier.release();
        return myTotal;
    }
}