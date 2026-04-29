package org.example.dsa;

public class palindrome {

    public static void main(String[] args) {

        String name = "HaraH";

        String name2 = "H a r a H";
        name2 = name2.replaceAll("\\s+","");

        String revername = new StringBuilder(name2).reverse().toString();
        System.out.println(revername);

       String res=  name.equals(revername) ?  "palindrom" : "notpalindrom" ;

        System.out.println(res);
    }
}
