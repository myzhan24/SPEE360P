/**
 * Created by Matt on 2/17/2015.
 */
public class CyclicBarrier {
    int total;
    public CyclicBarrier(int parties) {
// Creates a new CyclicBarrier that will trip when
// the given number of parties (threads) are waiting upon it
        total = parties;

    }
    public synchronized int await() throws InterruptedException {
// Waits until all parties have invoked await on this barrier.
// If the current thread is not the last to arrive then it is
// disabled for thread scheduling purposes and lies dormant until
// the last thread arrives.
// Returns: the arrival index of the current thread, where index
// (parties - 1) indicates the first to arrive and zero indicates
// the last to arrive.

        int myTotal=0;

        try{
            total--;
            myTotal = total;
            if(total==0)
                notifyAll();

            else
                wait();

        } finally {
            return myTotal;
        }

    }
}