package CodingINteview.problems;

public class Palindrome {


     public void ispalindrome(String str){

         String rev ="";

         for ( int i = str.length()-1 ; i >=0 ; i--){

             rev = rev + str.charAt(i);
         }

         System.out.println(rev.equals(str) ? "Palindrome" : "Not a Palindrome");
     }


    public static void main(String[] args) {

        String p = "madam";

        Palindrome obj = new Palindrome();

        obj.ispalindrome(p);

    }
}

