package org.example.problems;

/// given a string "abc" the method should return List<String> which should have all possible combinations of each character in the string.
///
/// output expected:
///
/// ["a","b","c","ab","bc","ac","abc"]


import java.util.*;

public class Possible_combinations {
    public static List<String> getCombinations(String str) {
        List<String> result = new ArrayList<>();

        for (char ch : str.toCharArray()) {
            int size = result.size();

            // add current character alone
            result.add(String.valueOf(ch));

            // append to existing combinations
            for (int i = 0; i < size; i++) {
                result.add(result.get(i) + ch);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(getCombinations("abc"));
    }
}



/// my first attempt but it will fail if more then 3 charactors

//public static List<String> method(Stri++++++++++++++++++++++++++++++++++
// ng name) {
//
//    List<String> list = new ArrayList<>();
//    String[] word = name.split("");
//
//    for (int i = 0; i < word.length; i++) {
//        list.add(word[i]);
//        for (int j = i + 1; j < word.length; j++) {
//
//            list.add(word[i] + word[j]);
//
//            for (int k = j + 1; k < word.length; k++) {
//                list.add(word[i] + word[j] + word[k]);
//            }
//        }
//    }
//
//    return list;
//}