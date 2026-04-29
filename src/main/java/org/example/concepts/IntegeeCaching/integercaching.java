package org.example.concepts.IntegeeCaching;

public class integercaching {

        public static void main(String[] args) {

            Integer a = 10;
            Integer b = 10;
            Integer c = 200;
            Integer d = 200;

            System.out.println(a==b); // true
            System.out.println(c==d); // false
        }

        /// /This uses Integer caching.
        ///
        /// Java caches Integer objects in range:
        ///
        /// -128 to 127
        ///
        /// So for values inside this range:
        ///
        /// No new object created
        ///
        /// Same cached object reused
        ///
        /// a and b point to same object


    }

