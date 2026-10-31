package CodingINteview.problems.math;

public class IsPrime {

    public static boolean isPrime(int n) {

        if (n == 2) return true;
        if ((n <= 1) || (n % 2 == 0))  return false;     // 0,1 are not prime// even numbers > 2 are not prime
             // 2 is prime


        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }


    public static void main(String[] args) {

        isPrime(23);
    }
}

