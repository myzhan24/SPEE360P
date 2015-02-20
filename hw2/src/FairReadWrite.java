import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Matt on 2/17/2015.
 */
public class FairReadWrite {
    ReentrantLock lock = new ReentrantLock();
    Condition writers = lock.newCondition();
    Condition readers = lock.newCondition();
    ReentrantLock mutexReadCount = new ReentrantLock();
    int numWriters = 0;
    int numReaders = 0;

    void beginRead(){
        lock.lock();
        try {
            while (numWriters != 0)
                try {
                    readers.await();        //reader wait
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            try {
                mutexReadCount.lock();              //reader count mutex
                numReaders++;
                if (numReaders == 1)             //reader waits for writer
                    try {
                        writers.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            } finally {
                mutexReadCount.unlock();            //reader count mutex unlock
            }

            readers.signal();               //signal next reader
        } catch (Exception e) {

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
    void beginWrite(){
        while(numWriters!=0 || numReaders!=0) {

            if(numReaders!=0)
                try {
                    readers.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            else if(numWriters!=0)
                try {
                    writers.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        numWriters++;

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