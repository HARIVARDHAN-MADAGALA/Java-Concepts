package org.example.Conversions;

import java.util.Arrays;

public class conversions {

    public static void main(String[] args) {

        int[] num = {1,2,3,4,5};

        int[] num2 = new int[5];

        String[] words = {"hari","vardhan","vikas", "swaroop"};

        char[] charc = {'a','b','c','d','e','f'};

        String name = "Hari Vardhan";

        char[] namechar = name.toCharArray();

        int left = 0;
        int right = namechar.length-1;

        while(left < right){

            char temp = namechar[left];
            namechar[left] = namechar[right];
            namechar[right] = temp;
            left ++;
            right --;
        }


        System.out.println(

                new String(namechar)
        );



    }
}
