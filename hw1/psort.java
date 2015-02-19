import jdk.jfr.events.ExceptionThrownEvent;
import sun.text.resources.cldr.xh.FormatData_xh;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by Matt on 2/2/2015.
 */
public class PSort implements Runnable{

    public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    static final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

    static int[] my_array;
    final int begin, end;


    // total array / number of processors available, used for determining when creating a new thread
    private static int minPartitionSize;

    public PSort(int minPSize,int[] array, int b, int e) {
        this.minPartitionSize = minPSize;
        my_array = array;
        begin = b;
        end = e;
    }


    /**
     * returns the index of the median value of the first, middle, and last element of an array
     * used for picking the pivot
     * @param A
     * @param begin
     * @param end
     * @return
     */
    public static int median(int[] A, int begin, int end) {
        int middle = (end - begin)/2;
        int a = A[begin];
        int b = A[end];
        int c = A[middle];

        // a is the median
        if((a >= c && a <= b)||(a >= b && a <= c))
        {
            return begin;
        }
        // b is the median
        else if((b >= c && b <= a)||(b >= a && b <= c))
        {
            return end;
        }
        //c is the median: A <= C <= B or B <= C <= A
        else if((c >= a && c <= b)||(c >= b && c <= a))
        {
            return middle;
        }
        else{
            System.out.println("Something's wrong with MEDIAN\nbegin\t");
            return begin;
        }

    }

    /**
     * swap
     * swaps two values in an array using index i and index j
     * @param A
     * @param i
     * @param j
     */
    public static void swap(int[] A, int i, int j) {
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }


    public static void parallelSort(int[] A, int begin, int end) {
        int length = end - begin + 1;
        //stop sorting if sub array is one place or less
        if(length <= 1)
            return;


        //Pick a pivot from the array, using the median of the start middle and end
        int pivotIndex = median(A, begin, end);
        int pivotValue = A[pivotIndex];

       // System.out.println(Arrays.toString(A)+" pivot: "+pivotValue+" for partition ["+begin+","+end+"]");

        //put chosen pivot at the end of the array
        swap(A, pivotIndex, end);

        //index for swapping elements lower than the pivot value
        int storeIndex = begin;
        for(int i = begin; i < end; i++) {
            if(A[i] <= pivotValue){
                swap(A, i, storeIndex);
                storeIndex++;
            }
        }

        //swap the pivot value and the storeIndex to place the pivot in the correct spot
        swap(A, storeIndex, end);


      //  System.out.println(Arrays.toString(A) + " splitting partitions\t["+begin+","+(storeIndex-1)+"]\t["+(storeIndex+1)+","+end+"]\n");
        if(length > minPartitionSize) { //if the length is greater than the min partition size, we can create an additional thread for executing one of the two subarrays that is partitioned

            //the new thread will get the subarray which is lower than the storeIndex(pivot value) to sort
            PSort psort = new PSort(minPartitionSize, A, begin, storeIndex-1);
            Future<?> future = executor.submit(psort);
            //the current thread will continue with the subarray which values are greater than the storeIndex to sort
            parallelSort(A, storeIndex+1, end);

          try{
              future.get(1000, TimeUnit.SECONDS);
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else { //we do not wish to create a new thread because the length of the subarray is too small for the number of processors available, so this thread will execute the two subarrays in sequence
            //all values before the store index are lower than the store index, and still need to be sorted
            parallelSort(A, begin, storeIndex - 1);

            //all values after the store index are higher than the store index and still need to be sorted
            parallelSort(A, storeIndex+1, end);
        }
    }

    @Override
    public void run() {
        parallelSort(my_array, begin, end);
      //  System.out.println(Arrays.toString(my_array)+" partition ["+begin+","+end+"] finished");
    }

    public static void main (String args[]) throws InterruptedException {


        //int[] arr = {5, 4, 3, 2, 1, 0};
        int[] arr = new int[9999];
        for (int i = 0; i < arr.length;i++){
            arr[i] = (int)(Math.random()*10);
        }

        PSort p1 = new PSort(arr.length/PSort.MAX_THREADS, arr, 0, arr.length-1);
        Thread t1 = new Thread(p1);
        t1.start();
        t1.join();
        /*while(!isSorted)
        {

        }*/
        //arr now has the sorted array
        System.out.println("Sorted Array:\t"+Arrays.toString(arr));
    }
}
