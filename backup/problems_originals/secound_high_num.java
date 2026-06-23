package org.example.problems;

public class secound_high_num {

    public static void main(String[] args) {

        int[] num = {1,233,3,4,52};
        int first = Integer.MIN_VALUE;
        int secound = Integer.MIN_VALUE;

        for(int i : num){
            if (i > secound){
                secound = i;
                if (i > first) {
                    secound = first;
                    first = i;
                }
            }
        }
        System.out.println(secound);
    }
}


/// for (int num : arr) {
///             if ( num > first) {
///                 second = first;
///                 first = num;
///             } else if (num != first && num > second) {
///                 second = num;
///             }
///         }

