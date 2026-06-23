package org.example.problems.strings;

public class reverse_a_String {

    public static void main(String[] args) {

        String a = "NAHDRAVIRAH";

        /// using stringbuilder

        String b = new StringBuilder(a).reverse().toString();

        /// using string concetation

        String rev ="";

        for ( int i = a.length()-1; i >= 0 ; i--)
        {

            rev = rev + a.charAt(i);
        }



    /// using stringbuilder without reverse

    StringBuilder str = new StringBuilder();

    for ( int i = a.length()-1; i >= 0 ; i--)
    {

        str = str.append(a.charAt(i));
    }


        System.out.println(str);

    }
}

