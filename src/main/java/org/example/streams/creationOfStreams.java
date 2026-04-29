package org.example.streams;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class    creationOfStreams {

    public static void main(String[] args) {


        int[] arr = {1,2,3,4};

        List<Integer> list = Arrays.asList(1,2,3,4,5);

         list.stream();
         list.parallelStream();
         Stream.of(1,2,3,4);
         Arrays.stream(arr);
         Stream.of(arr);
    }

}
/*
==================== JAVA STREAM CREATION — CHEAT SHEET ====================

1) FROM COLLECTION
---------------------------------------------------------------------------
List<Integer> list = List.of(1,2,3);

list.stream();          // Sequential stream
list.parallelStream();  // Parallel stream


2) FROM ARRAYS
---------------------------------------------------------------------------
// Object array
String[] arr1 = {"A","B","C"};

Arrays.stream(arr1);    // A B C
Stream.of(arr1);        // A B C  (varargs — works fine)

// Primitive array (IMPORTANT)
int[] arr2 = {1,2,3};

Arrays.stream(arr2);    // IntStream → 1 2 3  ✅
Stream.of(arr2);        // Stream<int[]> → one element ❌


3) FROM VALUES
---------------------------------------------------------------------------
Stream.of(1,2,3,4);
Stream.of("A","B","C");


4) EMPTY STREAM
---------------------------------------------------------------------------
Stream.empty();


5) INFINITE STREAMS
---------------------------------------------------------------------------
// iterate — sequential pattern
Stream.iterate(0, n -> n + 1)
      .limit(5);

// generate — supplier-based
Stream.generate(Math::random)
      .limit(3);


6) PRIMITIVE STREAMS (NO BOXING)
---------------------------------------------------------------------------
// IntStream
IntStream.of(1,2,3);
IntStream.range(1,5);        // 1 2 3 4
IntStream.rangeClosed(1,5);  // 1 2 3 4 5

// LongStream
LongStream.of(1L,2L,3L);

// DoubleStream
DoubleStream.of(1.1,2.2,3.3);


7) FROM STRING
---------------------------------------------------------------------------
// Characters (Unicode values)
String s = "ABC";

s.chars();   // IntStream → 65 66 67

// Convert to characters
s.chars()
 .mapToObj(c -> (char)c);

// Lines (Java 11+)
"A\nB\nC".lines();


8) FROM FILES
---------------------------------------------------------------------------
Files.lines(Path.of("file.txt"));   // Stream<String> (each line)


9) FROM OPTIONAL (Java 9+)
---------------------------------------------------------------------------
Optional<String> opt = Optional.of("A");

opt.stream();


10) BUILDER PATTERN
---------------------------------------------------------------------------
Stream<Integer> s =
    Stream.<Integer>builder()
          .add(1)
          .add(2)
          .add(3)
          .build();


==================== QUICK DECISION GUIDE ====================

Collection       → collection.stream()
Object array     → Arrays.stream(arr)
Primitive array  → Arrays.stream(arr)   ⚠️
Values           → Stream.of(...)
Range numbers    → IntStream.range(...)
Infinite         → iterate() / generate()
String chars     → s.chars()
File lines       → Files.lines()
Optional         → optional.stream()

================================================================
*/
