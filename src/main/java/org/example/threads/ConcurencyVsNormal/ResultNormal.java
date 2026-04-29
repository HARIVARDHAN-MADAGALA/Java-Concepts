package org.example.threads.ConcurencyVsNormal;

import java.util.*;

class ResultNormal {

    public static long computeFactorialSum(List<Long> inputs) {

        long sum = 0;

        for (Long num : inputs) {
            sum += computeFactorial(num);
        }

        return sum;
    }

    public static Long computeFactorial(Long num) {
        Long factorial = 1L;

        for (int i = 1; i <= Math.abs(num); i++) {
            factorial *= i;
        }

        return factorial;
    }
}
