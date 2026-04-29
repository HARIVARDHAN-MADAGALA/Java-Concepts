package org.example.exceptions;

import java.io.FileReader;
//import java.io.IO;
import java.io.IOException;

public class thows {

//If a method may throw a checked exception, you must declare it:

    public void readFile() throws IOException {
        FileReader fr = new FileReader("abc.txt");  // FileNotFoundException (checked)
    }

    public static void main(String[] args) {
        thows obj = new thows();

        //And whoever calls it must handle it:
        try{

            obj.readFile();


        }
        catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }



    }
}
