package CodingINteview.SlidingWindow;

import java.util.Arrays;

public class Solution {
    public static int substringAnagrams(String s, String t) {
        int lenS = s.length();
        int lenT = t.length();
        
        // Handle edge case where target string is longer than source string
        if (lenT > lenS) {
            return 0;
        }
        
        int count = 0;
        int[] expectedFreqs = new int[26];
        int[] windowFreqs = new int[26];
        
        // Populate expectedFreqs with the characters in string 't'
        for (int i = 0; i < lenT; i++) {
            expectedFreqs[t.charAt(i) - 'a']++;
        }
        
        int left = 0;
        int right = 0;
        
        while (right < lenS) {
            // Add the character at the right pointer to windowFreqs
            windowFreqs[s.charAt(right) - 'a']++;
            
            // Check if the window has reached the expected fixed length
            if (right - left + 1 == lenT) {
                // Arrays.equals checks if both frequency tables match perfectly
                if (Arrays.equals(windowFreqs, expectedFreqs)) {
                    count++;
                }
                
                // Remove the character at the left pointer from windowFreqs
                windowFreqs[s.charAt(left) - 'a']--;
                left++;
            }
            right++;
        }
        
        return count;
    }

    // Driver code to test the implementation
    public static void main(String[] args) {
        String s = "cbaebabacd";
        String t = "abc";
        System.out.println("Anagram count: " + substringAnagrams(s, t)); // Output: 2
    }
}
