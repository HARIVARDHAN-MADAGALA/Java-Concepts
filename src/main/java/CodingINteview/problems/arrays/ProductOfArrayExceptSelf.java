package CodingINteview.problems.arrays;

import java.util.Arrays;

public class ProductOfArrayExceptSelf {

    public static int[] productOfArrayExceptSelf(int[] arr){

        int lenght = arr.length;

        int[] result = new int[lenght];
        int product = 1;
        result[0] = 1;

        //fill left products in result array -> [1,1,2,6]
        for(int i = 1; i <lenght; i++ ){

            product = product * arr[i-1];
            result[i] = product;
        }
        System.out.println(Arrays.toString(result));

        //product result {1,1,2,6} with arr in reverse direction

        product = 1;
        for(int i = lenght-1; i > 0 ; i--){

            product = product * arr[i];
            result[i-1] = result[i-1] * product;
        }

        return result;
    }
    public static void main(String[] args) {

        int[] arr = {1,2,3,4};

        int[] result = productOfArrayExceptSelf(arr);

        System.out.println(Arrays.toString(result));
    }
}

