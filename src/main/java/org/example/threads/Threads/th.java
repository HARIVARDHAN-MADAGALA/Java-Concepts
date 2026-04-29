package org.example.threads.Threads;

class th{

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {for (int i = 1; i <= 5; i++) {
            System.out.println("t1" + ": " + i);


        }});

        Thread t2 = new Thread(() -> {for (int i = 1; i <= 5; i++) {
            System.out.println("t2" + ": " + i);

        }});

        Thread t3 = new Thread(() -> {for (int i = 1; i <= 5; i++) {
            System.out.println("t3" + ": " + i);
        }});

//        t1.setPriority(Thread.MAX_PRIORITY);
//        t2.setPriority(Thread.NORM_PRIORITY);
//        t3.setPriority(Thread.MIN_PRIORITY);
        System.out.println(t1.getState());

        t1.start();
        System.out.println(t1.getState());
        t1.join();
        System.out.println(t1.getState());

        t2.start();
//        t2.join();
//        t3.start();
//        t3.join();// Starts thread (calls run() internally)


    }
}

// Usage:






















//Method 1: Extending Thread Class
//class MyThread extends Thread {
//    @Override
//    public void run() {
//        for (int i = 1; i <= 5; i++) {
//            System.out.println(Thread.currentThread().getName() + ": " + i);
//            try {
//                Thread.sleep(500);  // Sleep for 500ms
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
/// / Usage:
//MyThread t1 = new MyThread();
//MyThread t2 = new MyThread();
//t1.start();  // Starts thread (calls run() internally)
//t2.start();


//Method 2: Implementing Runnable Interface (Better!)
//
//class MyTask implements Runnable {
//    @Override
//    public void run() {
//        for (int i = 1; i <= 5; i++) {
//            System.out.println(Thread.currentThread().getName() + ": " + i);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//// Usage:
//Thread t1 = new Thread(new MyTask(), "Thread-1");
//Thread t2 = new Thread(new MyTask(), "Thread-2");
//t1.start();
//t2.start();


//Method 3: Lambda Expression (Java 8+)
//
//Thread t1 = new Thread(() -> {
//    for (int i = 1; i <= 5; i++) {
//        System.out.println(Thread.currentThread().getName() + ": " + i);
//    }
//});
//t1.start();