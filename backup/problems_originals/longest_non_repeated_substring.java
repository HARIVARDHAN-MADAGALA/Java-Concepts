package CodingINteview.problems.strings;
import java.util.*;

public class longest_non_repeated_substring {

//    public int lengthOfLongestSubstring (String s){
//        // Map to store character and its latest index
//        Map<Character, Integer> map = new HashMap<>();
//        int maxLength = 0;
//        int start = 0;  // Left pointer of window
//
//        // Right pointer moves through string
//        for (int end = 0; end < s.length(); end++) {
//            char currentChar = s.charAt(end);
//
//            // If character already exists in current window
//            if (map.containsKey(currentChar)) {
//                // Move start pointer to right of previous occurrence
//                start = Math.max(start, map.get(currentChar) + 1);
//            }
//
//            // Update character's latest position
//            map.put(currentChar, end);
//
//            // Calculate max length
//            maxLength = Math.max(maxLength, end - start + 1);
//        }
//
//        return maxLength;
//    }

    public int lengthOfLongestSubstring (String s){

         int start = 0;
         int maxLenght = 0;
         Map<Character, Integer> map = new HashMap<>();



         for ( int end =0 ; end < s.length() ; end ++) {

             if (map.containsKey(s.charAt(end))) {

                 start = Math.max(start, map.get(s.charAt(end))+1);
             }


             map.put(s.charAt(end), end);

             maxLenght = Math.max(maxLenght, end - start + 1);

         }

        return maxLenght;
    }


    public static void main(String[] args) {

        String name = "abcabdab";

        longest_non_repeated_substring obj = new longest_non_repeated_substring();
        System.out.println(obj.lengthOfLongestSubstring(name));



    }



    }


    /// if u want to print word

//public static String longestSubstring(String s) {
//
//    int start = 0;
//    int maxLen = 0;
//
//    int startIndex = 0;
//    int endIndex = 0;
//
//    Map<Character, Integer> map = new HashMap<>();
//
//    for (int end = 0; end < s.length(); end++) {
//
//        char c = s.charAt(end);
//
//        if (map.containsKey(c)) {
//            start = Math.max(start, map.get(c) + 1);
//        }
//
//        map.put(c, end);
//
//        int windowLen = end - start + 1;
//
//        if (windowLen > maxLen) {
//            maxLen = windowLen;
//            startIndex = start;
//            endIndex = end;
//        }
//    }
//
//    return s.substring(startIndex, endIndex + 1);
//}


