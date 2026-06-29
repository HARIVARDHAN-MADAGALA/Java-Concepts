package CodingINteview.problems.strings;

import java.util.LinkedHashMap;
import java.util.Map;

public class FirstNonRepeatingChar {

    public static Character getNonRepeatingChar(String word){

        if(word == null || word.isEmpty()) return null;

        Map<Character,Integer> wordmap = new LinkedHashMap<>();

        for(char c : word.toCharArray()){

            wordmap.put(c,wordmap.getOrDefault(c,0)+1);
        }

        for(Map.Entry<Character,Integer> m : wordmap.entrySet()){

            if(m.getValue() == 1) { return m.getKey();}
        }

        return null;
    }

    public static void main(String[] args) {

        String word = "swiss";

        System.out.println(getNonRepeatingChar(word));
    }
}

