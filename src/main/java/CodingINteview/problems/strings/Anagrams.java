package CodingINteview.problems.strings;

/// anagrams  =  same set of letters
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class Anagrams {

    public static void main(String[] args) {

        String s1 = "silent";
        String s2 = "listen";

        char[] s3 = s1.toLowerCase().toCharArray();
        char[] s4 = s2.toLowerCase().toCharArray();

        Arrays.sort(s3);
        Arrays.sort(s4);

        System.out.println(Arrays.equals(s3,s4));


//        TreeSet<String> tree1 = new TreeSet<>(new ArrayList<>(Arrays.asList(s1.split(""))));
//
//        TreeSet<String> tree2 = new TreeSet<>(new ArrayList<>(Arrays.asList(s2.split(""))));
//
//        System.out.println(tree1.equals(tree2));


    }
}

