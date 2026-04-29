package org.example.Conversions;

public class chararray_to_string {

    public static void main(String[] args) {

        char[] charArr = { 'a','d','g',' ','g'};

        String name = new String(charArr);

        System.out.println(

                name
        );

        System.out.println(

                String.valueOf(charArr)
        );
    }
}
