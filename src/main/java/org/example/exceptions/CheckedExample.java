package org.example.exceptions;

import java.io.*;

public class CheckedExample {


    /// Checked exceptions must be handled using either try-catch or throws.

    /// We only write throws at the method (or constructor) level.

//    HANDLE WIH TRY CATCH
    public static void main(String[] args) {
        try {
            FileReader reader = new FileReader("abc.txt");  // checked exception
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }


    // HANDLE WITH THROWS
//
//    public static void main(String[] args) throws FileNotFoundException {
//                    FileReader reader = new FileReader("abc.txt");  // checked exception
//
//    }




}

