package org.example.Conversions;

public class Substring {

    public static void main(String[] args) {

        String one = "KINGKONG";

        String two = "KONG";

        int d = one.indexOf(two);
        System.out.println(d);

        boolean f = one.contains(two);
        System.out.println(f);

        char fd = one.charAt(2);
        System.out.println(fd);

        String three = one.substring(0,one.length()-1);
        System.out.println(three);

        System.out.println(one.substring(4,5));
    }
}
