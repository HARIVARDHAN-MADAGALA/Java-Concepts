package org.example.threads.ConcurencyVsNormal;

import java.util.*;
import java.util.concurrent.*;

class Result {

    public static long computeFactorialSum(List<Long> inputs) {

        ExecutorService executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        List<Future<Long>> futures = new ArrayList<>();

        // submit tasks
        for (Long num : inputs) {
            futures.add(executor.submit(() -> computeFactorial(num)));
        }

        long sum = 0;

        // collect results
        for (Future<Long> f : futures) {
            try {
                sum += f.get(); // waits for result
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

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
