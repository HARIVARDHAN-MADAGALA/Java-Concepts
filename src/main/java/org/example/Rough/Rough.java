package org.example.Rough;

import org.example.genrics.Box;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Rough {


    public static void main(String[] args) {

        String name = "Harivardhan";

        System.out.println(

                name.chars().mapToObj(c -> (char)c).collect(Collectors.groupingBy(c->c,Collectors.counting()))
        );
    }
    }

