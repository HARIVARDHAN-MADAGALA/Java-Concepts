package org.example.problems;

//         Remove consecutive duplicate characters
//Input: "Suhhaaannii"
//Output: "Suhani"

import java.util.ArrayList;
import java.util.List;

public class RemoveConsecutiveDulplicates {

    public static String method(String word){

        StringBuilder result = new StringBuilder();
        char lastOccur = 0;

        for( char c : word.toCharArray()){

            if( !(lastOccur == c) ){
                result.append(c);
            }

            lastOccur = c;
        }return String.valueOf(result);
    }
    public static void main(String[] args) {

        String word = " hello";

        System.out.println(

                method(word)
        );
    }
}
