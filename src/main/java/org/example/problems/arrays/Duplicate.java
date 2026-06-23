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

