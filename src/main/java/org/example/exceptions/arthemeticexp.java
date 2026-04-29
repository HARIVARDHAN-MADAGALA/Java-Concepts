package org.example.exceptions;

public class arthemeticexp {


    public static void main(String[] args) {

        try {
            int a = 10 / 0;
        }
        catch (ArithmeticException e){
            System.out.println("Error : " + e.getMessage());
        }

        finally {
            System.out.println("printing finally");
        }

        int[] a = new int[4];

        // multiple try-catch
        try{
            a[10] =100;
        }

        catch (ArithmeticException e){
            System.out.println(e.getMessage());
        }
        catch (ArrayIndexOutOfBoundsException b){
            System.out.println(b.getMessage());
        }

//You can manually throw exceptions:
//        throw new IllegalArgumentException("Invalid input!");
        throw new Customexception("created exception");

    }
}


//✅ Summary in One Line:
//Exceptions in Java are objects representing errors.
// They can be checked (must handle) or unchecked (runtime errors).
// Use try-catch-finally or throws, throw custom ones if needed, and always handle them properly.

