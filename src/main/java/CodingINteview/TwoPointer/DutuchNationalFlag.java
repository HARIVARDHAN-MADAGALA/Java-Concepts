package CodingINteview.TwoPointer;

import java.util.Arrays;

/// Given an array containing only 0, 1, and 2, sort it in-place.
///
/// Example
/// Input:
/// [2,0,2,1,1,0]
///
/// Output:
/// [0,0,1,1,2,2]
///
public class DutuchNationalFlag {

    public static int[] sortInPlace(int[] arr){

        int writeZero = 0;
        int writeTwo = arr.length - 1;
        int read = 0;


        while ( read <= writeTwo) {

                if (arr[read] == 0) {

                    int temp = arr[writeZero];
                    arr[writeZero] = arr[read];
                    arr[read] = temp;

                    writeZero ++;
                    read++;
                }

                else if(arr[read] == 1) read++;

                else {

                    int temp = arr[writeTwo];
                    arr[writeTwo] = arr[read];
                    arr[read] = temp;

                    writeTwo --;
                }


        }

        return arr;
    }

    public static void main(String[] args) {

        int[] arr = {2,0,2,1,1,0};

        int[] result = sortInPlace(arr);

        System.out.println(Arrays.toString(result));


    }
}
