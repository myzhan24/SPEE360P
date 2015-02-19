/**
 * Created by Matt on 2/17/2015.
 */
public class FifoLock {
    int ticket=0;
    int turn=0;
    synchronized int getTicket(){
        return ticket++;
    }
    synchronized void requestCS(int ticketNumber) throws InterruptedException {
        while(turn!=ticketNumber)
            wait();

    }
    synchronized void releaseCS(){
        turn++;
        notifyAll();
    }
}
