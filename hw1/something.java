import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Matt on 2/2/2015.
 */
public class PSearch implements Callable {

    public static List<Future<Integer>> indices = new ArrayList<Future<Integer>>();
    static final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
    public int[] myArray;
    public int x;
    public int numThreads;

    public PSearch(int x, int[] arr, int numThreads) {
        this.x=x;
        this.myArray = arr;
        this.numThreads = numThreads;
    }


    public static int parallelSearch(int x, int[] A, int numThreads) {
// your implementation goes here.



        //There is only one thread so iterate part of the array
        if(numThreads == 1) {

            for(int i : A){
                if(A[i]==x)
                    return 1;
            }

        } else { //divide the array up and give them to separate threads
            int quo = A.length/numThreads; // quotient of length/numThreads
            int rem = A.length%numThreads; // remainder of length/numThreads

            for(int i = 0; i < (numThreads-1); i++)
            {
                PSearch psearch = new PSearch(x, Arrays.copyOfRange(A,i*quo,(i+1)*quo), 1);

            }

            for(int i = (numThreads-1)*quo; i < A.length;i++)
            {
                if(A[i]==x)
                    return 1;
            }
        }


        return -1;
    }

    @Override
    public Integer call() throws Exception {
        return parallelSearch(this.x,this.myArray,this.numThreads);
    }

    public static void main(String args[]) {
        int[] arr = new int[20];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = (int)(Math.random()*10);
        }

        // max threads used
        int numThreads = Runtime.getRuntime().availableProcessors();





    }
}