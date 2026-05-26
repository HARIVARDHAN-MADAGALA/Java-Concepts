package org.example.problems;

import java.util.Arrays;
import java.util.List;

public class CommomPrefix {

    public static String method(List<String> list) {

        if (list == null || list.isEmpty()) return ""; // 🔒 guard clause

        String longest = list.get(0); // camelCase fix

        for (int i = 1; i < list.size(); i++) {
            int c = 0; // declare inside loop — cleaner scope

            while (c < Math.min(longest.length(), list.get(i).length())) {
                if (longest.charAt(c) != list.get(i).charAt(c)) {
                    break;
                }
                c++;
            }

            longest = longest.substring(0, c);
        }

        return longest;
    }

    public static void main(String[] args) {

        List<String> list = Arrays.asList("inter", "interview", "internal");

        System.out.println(

                method(list)
        );
    }
}



//public static String method(List<String> list){
//
//        String result = "";
//
//        for( String word : list){
//            if ( result.isEmpty()){
//                result = word;
//                continue;
//            }
//            while( !word.startsWith(result)){
//                result = result.substring(0,result.length()-1);
//            }
//        }
//        return  result;
//    }
