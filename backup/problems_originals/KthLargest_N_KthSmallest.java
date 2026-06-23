package org.example.problems.heaps;

//Find Kth largest and Kth smallest element
//Example: [3,2,1,5,6,4], k = 2

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class KthLargest_N_KthSmallest {

    public static int[] method(int[] arr, int k){

        // for finding Kth largest
        int[] result = new int[2];

        PriorityQueue<Integer> p1 = new PriorityQueue<>();

        for(int num : arr){

            p1.offer(num);

            if(p1.size() > k){
                p1.poll();
            }
        }

        result[0] = p1.peek();

        // for finding Kth smallest

        PriorityQueue<Integer> p2 = new PriorityQueue<>(Comparator.reverseOrder());

        for(int num : arr){

            p2.offer(num);

            if(p2.size() > k){
                p2.poll();
            }
        }

        result[1] = p2.peek();

        return result;

    }
    public static void main(String[] args) {

        int[] arr = {3,2,1,5,6,4};

        System.out.println(

                Arrays.toString( method(arr,3))
        );

    }
}


