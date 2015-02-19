import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Matt on 2/17/2015.
 */
public class Garden {
    int maxHoles;
    int emptyHoles;     //dug, no seed
    int unfilledHoles;  //seeded, not filled
    boolean shovelFree = true;
    ReentrantLock mutex = new ReentrantLock();
    Condition shovel =  mutex.newCondition();
    Condition maryFill = mutex.newCondition();
    Condition newtonDig = mutex.newCondition();
    Condition benSeed = mutex.newCondition();



    public Garden(int MAX){
        maxHoles = MAX;
        emptyHoles = 0;
        unfilledHoles = 0;
    }
    public void startDigging() throws InterruptedException {//newton
        mutex.lock();
        try{

            while(unfilledHoles>=maxHoles || !shovelFree) {
                if(unfilledHoles>=maxHoles)
                    maryFill.await();
                else if(!shovelFree)
                    shovel.await();
            }
            shovelFree = false;
        } finally {
            mutex.unlock();
        }
    }
    public void doneDigging(){//newton
        mutex.lock();
        try{


        emptyHoles++;
            shovelFree= true;
        shovel.signalAll();
        newtonDig.signalAll();
        } finally {
            mutex.unlock();
        }
    }


    public void startSeeding() throws InterruptedException {//benjamin
        mutex.lock();
        try{
            while(emptyHoles==0)
                newtonDig.await();
        } finally {
        mutex.unlock();
        }
    }
    public void doneSeeding(){///benjamin
        mutex.lock();
        try{
            emptyHoles--;
            unfilledHoles++;

            benSeed.signalAll();
        }finally {
            mutex.unlock();
        }
    }


    public void startFilling() throws InterruptedException {//mary
        mutex.lock();
        try{
            while(unfilledHoles==0 || !shovelFree) {
                if(unfilledHoles==0)
                    benSeed.await();
                else if(!shovelFree)
                    shovel.await();
            }
            shovelFree = false;
        } finally {
            mutex.unlock();
        }
    }
    public void doneFilling(){//mary
        mutex.lock();
        try{
            unfilledHoles--;
            if(unfilledHoles<maxHoles)
                maryFill.signalAll();
            shovelFree = true;
            shovel.signalAll();

        }finally {
            mutex.unlock();
        }
    }
}
