import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Matt on 2/17/2015.
 */
public class FairReadWrite {
    ReentrantLock lock = new ReentrantLock();
    Condition writers = lock.newCondition();
    Condition readers = lock.newCondition();
    int numWriters = 0;
    int numReaders = 0;

    void beginRead() throws InterruptedException {
        lock.lock();
        try{
            while(numWriters!=0)
                readers.await();

            numReaders++;
        } finally {
            lock.unlock();
        }
    }
    void endRead() {
        lock.lock();
        try{
            numReaders--;
            if(numReaders==0)
                writers.signal();


        } finally {
            lock.unlock();
        }
    }
    void beginWrite() throws InterruptedException {
        lock.lock();
        try{
            while(numWriters!=0 || numReaders!=0)
                writers.await();

            numWriters++;
        } finally {
            lock.unlock();
        }
    }
    void endWrite(){
        lock.lock();
        try{
            numWriters--;
            writers.signal();
            readers.signalAll();
        }finally {
            lock.unlock();
        }
    }
}
