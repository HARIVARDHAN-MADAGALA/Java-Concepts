package org.example.problems.strings;

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



//for(int i=0; i < a.length(); i++){
//
//            char ch = a.charAt(i);
//
//            if( a.indexOf(ch) == a.lastIndexOf(ch)) {
//                System.out.println(a.charAt(i));break;
//            }



// frequency
//                word.chars().mapToObj(c->(char)c)
//                        .filter(c -> Collections.frequency((Arrays.asList(word.split(""))),String.valueOf(c)) ==1)
//                        .findFirst().orElse('#')

//grouping

//                word.chars().mapToObj(c -> (char)c)
//                        .collect(Collectors.groupingBy(c->c,LinkedHashMap::new,Collectors.counting()))
//                        .entrySet().stream().filter(c-> c.getValue() ==1).map(c->c.getKey())
//                        .findFirst().orElse('#')

