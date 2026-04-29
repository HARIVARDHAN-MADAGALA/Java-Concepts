package org.example.threads.Completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Demo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // CompletableFuture equivalent of Runnable thread
        CompletableFuture<Void> future =
                CompletableFuture.runAsync(() -> {
                    System.out.println("I am in CompletableFuture");
                });

        System.out.println("Main thread continues...");

        // Wait for async task to finish (optional)
        future.join();
    }
}



/// ⭐ Explanation (simple)
/// ✔ runAsync()
///
/// This replaces new Thread(obj).start()
///
/// ✔ () -> { ... }
///
/// This replaces your run() method.
///
/// ✔ future.join()
///
/// This waits for async job to finish (similar to thread.join()).

