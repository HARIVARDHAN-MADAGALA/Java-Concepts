package org.example.concepts.Lamda_FuntionalInterace;

@FunctionalInterface
interface Calculator {

    int add(int a, int b);
}


// 6. Separate implementation class
class MyCalculator implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}


// Used for method references
class MathUtil {

    // 7. Static method
    static int add(int a, int b) {
        return a + b;
    }

    // 8. Instance method
    int addNumbers(int a, int b) {
        return a + b;
    }
}


public class Main {

    public static void main(String[] args) {

        // 1. Lambda - normal
        Calculator c1 = (a, b) -> a + b;
        System.out.println("1. Lambda: " + c1.add(10, 20));


        // 2. Lambda - explicit parameter types
        Calculator c2 = (int a, int b) -> a + b;
        System.out.println("2. Lambda with types: " + c2.add(10, 20));


        // 3. Lambda - with return
        Calculator c3 = (a, b) -> {
            return a + b;
        };
        System.out.println("3. Lambda with return: " + c3.add(10, 20));


        // 4. Lambda - multiple statements
        Calculator c4 = (a, b) -> {
            int result = a + b;
            System.out.println("   Inside lambda: " + result);
            return result;
        };

        System.out.println("4. Multiple statements: " + c4.add(10, 20));


        // 5. Anonymous class
        Calculator c5 = new Calculator() {

            @Override
            public int add(int a, int b) {
                return a + b;
            }
        };

        System.out.println("5. Anonymous class: " + c5.add(10, 20));


        // 6. Separate implementation class
        Calculator c6 = new MyCalculator();

        System.out.println("6. Implementation class: " + c6.add(10, 20));


        // 7. Static method reference
        Calculator c7 = MathUtil::add;

        System.out.println("7. Static method reference: " + c7.add(10, 20));


        // 8. Instance method reference
        MathUtil obj = new MathUtil();

        Calculator c8 = obj::addNumbers;

        System.out.println("8. Instance method reference: " + c8.add(10, 20));


        // 9. Direct lambda invocation
        int result =
                ((Calculator) (a, b) -> a + b)
                        .add(10, 20);

        System.out.println("9. Direct lambda: " + result);
    }
}