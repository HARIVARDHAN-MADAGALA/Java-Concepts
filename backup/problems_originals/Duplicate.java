package org.example.problems.arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Duplicate {


//    public void duplicates(String name){
//
//        for( int i =0 ; i < name.length() ; i++){
//
//            if (i == name.lastIndexOf( name.charAt(i))   &&
//                    !name.substring(0,i).contains(String.valueOf(name.charAt(i)))) {
//                System.out.println(name.charAt(i));
//            }
//        }
//
//    }

    public static void main(String[] args) {

        String word = "HARIVARDHAN";

//        Duplicate s = new Duplicate();
//
//        s.duplicates("HARIVADHAN");

        char[] charword = word.toCharArray();

        Map<Character,Integer> charcountmap = new HashMap<>();

        for ( char c : charword){

            charcountmap.put(c, charcountmap.getOrDefault(c,0)+1);
        }

        for ( Map.Entry<Character,Integer> entry : charcountmap.entrySet()){

            if ( entry.getValue()>1){

                System.out.println(entry.getValue()+" "+entry.getKey());
            }
        }
        System.out.println(charcountmap);

    }
}


// In Streams

//  word.chars().mapToObj(c -> (char)c)
//        .collect(Collectors.groupingBy( c->c , Collectors.counting()))
//        .entrySet().stream()
//        .filter( c-> c.getValue() > 1)
//        .map(c-> c.getKey())
//        .toList();

//2
//public static Set<Integer> findDuplicates(List<Integer> list) {
//
//        Set<Integer> seen = new HashSet<>();
//        Set<Integer> duplicates = new HashSet<>();
//
//        for (Integer num : list) {
//            if (!seen.add(num)) {   // add() returns false if element already exists
//                duplicates.add(num);
//            }
//        }
//
//        return duplicates;
//    }




