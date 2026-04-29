package org.example.threads.others;
import java.util.concurrent.*;


public class callable  {



    public static void main(String[] args) throws ExecutionException, InterruptedException  {

    Callable<Integer> task = () -> {
        System.out.println("Calculating...");
        Thread.sleep(1000);
        return 42; // result
    };




        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(task);

        System.out.println("Result: " + future.get()); // blocks until result = 42
        executor.shutdown();
    }

}

//info

//🧩 What Happens Internally
//
//When you call executor.submit(callable.md), it wraps your Callable inside a FutureTask.
//
//The thread pool runs it in a background thread.
//
//The Future object you get immediately lets you:
//
//get() → wait for and return result
//
//cancel() → cancel the task
//
//isDone() / isCancelled() → check status
//
//So Callable enables true concurrent computations that return results, not just fire-and-forget tasks.



//Here’s how they’re connected internally:
//
//Callable  →  FutureTask  →  Future  →  ExecutorService
//
//Concept	Purpose
//Callable	Defines a task that returns a value (V call() method).
//Future	A handle to retrieve that value later.
//ExecutorService	Runs the Callable in a thread and gives you a Future.
