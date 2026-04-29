package org.example.Collections.array;

import java.util.*;

public class Array {

    public static void main(String[] args) {

        int[] array = new int[5];

        array[0]=1;array[1]=2;array[2]=3;array[3]=4;array[4]=5;

        int[] array2 = {1,2,3,4,5};

        List<Integer> l = Arrays.asList(1,24,5);

        for(int i=0 ; i<l.size() ; i++){
//            l.add(233);
        }

        for (int i : l) {
            l.remove(i); // 💥 CME
        }

    }
}
