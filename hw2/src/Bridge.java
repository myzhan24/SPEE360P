import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Matt on 2/17/2015.
 */
public class Bridge {
    ReentrantLock bridge = new ReentrantLock();
    Condition laneOpen = bridge.newCondition();
    int dir = 0;
    int numCars = 0;

    void arriveBridge(int direction) throws InterruptedException {
        bridge.lock();
        try {


            while (numCars > 3 || (direction != dir&&numCars>0))
                laneOpen.await();

                dir = direction;

            numCars++;
        }
        finally {
            bridge.unlock();
        }
    }
    void exitBridge(){
        bridge.lock();
        try{


        numCars--;
        if(numCars<4)
            laneOpen.signalAll();}
        finally {
            bridge.unlock();
        }
    }
}
