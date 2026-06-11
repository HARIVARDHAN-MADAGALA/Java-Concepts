package org.example.concepts.comparator_comparable;

import org.example.concepts.Innerclass.staic_innerclass.A;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class main {

    public static void main(String[] args) {


//        Comparable
        Student s1 = new Student(1,"lion");
        Student s2 = new Student(2,"goat");
        Student s3 = new Student(3,"hari");


        TreeSet<Student> set = new TreeSet<>();

        set.add(s1);
        set.add(s2);
        set.add(s3);

        List<Student2> list = new ArrayList<>();

        Student2 s12 = new Student2(1,"lion");
        Student2 s22 = new Student2(2,"goat");
        Student2 s32 = new Student2(3,"hari");

        list.add(s12);
        list.add(s22);
        list.add(s32);

        Collections.sort(list,(a,b) -> b.name.compareTo(a.name));
        list.sort((a,b) -> b.name.compareTo(a.name));

        Comparable<A> a = new Comparable<A>() {
            @Override
            public int compareTo(A o) {
                return 0;
            }
        };



        System.out.println(list);



/// Collections.sort() expects one of these:
///
/// Objects implement Comparable ✅
/// You provide a Comparator ✅

        /// Operator '-' cannot be applied to String
        ///
        /// 👉 Because:
        ///
        /// - works only on numeric types (int, double, etc.)
        /// getName() returns a String




//        List<Student> list = new ArrayList<>();
//
//        list.add(s2);
//        list.add(s3);
//        list.add(s1);
//
//
//        list.stream().forEach(c-> System.out.println(c.name));
//
//            Collections.sort(list);
//
//        list.stream().forEach(c-> System.out.println(c.name));
//
//
//
//
//
///       Comparator
//        Student2 n1 = new Student2(1,"hari");
//        Student2 n2 = new Student2(2,"goat");
//        Student2 n3 = new Student2(3,"lion");
//
//        List<Student2> list2 = new ArrayList<>();
//
//        list2.add(n2);
//        list2.add(n3);
//        list2.add(n1);
//
//        Collections.sort(list2,Comparator.comparing(c->c.name));
////        list2.stream().forEach(c-> System.out.println(c.name));
//
//
//        //Comparator
//
////        Collections.sort(list2, new Student2());
////        list2.stream().forEach(c-> System.out.println(c.name));
////




    }
}
