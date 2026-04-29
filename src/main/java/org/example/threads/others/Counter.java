package org.example.threads.others;


// RACE CONDITION //////////////////

import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private int count = 0;
    private static final ReentrantLock a = new ReentrantLock();

    public  void  increment() {
        count++;  // NOT atomic! (read, increment, write)
    }

    public int getCount() {
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
// Multiple threads incrementing
Counter counter = new Counter();

Thread t1 = new Thread(() -> {
    for (int i = 0; i < 100000; i++) {
        counter.increment();
    }
});

Thread t2 = new Thread(() -> {
    for (int i = 0; i < 100000; i++) {
        counter.increment();
    }
});




t1.start();
t2.start();
t1.join();
t2.join();

System.out.println(counter.getCount());  // Expected: 2000, Actual: 1800 (varies!)
// Race condition! Lost updates!
} }



//SOLUTION///////////////////



//Solution 1: Synchronized Method
//class Counter {
//    private int count = 0;
//
//    // Only ONE thread can execute this at a time
//    public synchronized void increment() {
//        count++;
//    }
//
//    public int getCount() {
//        return count;
//    }
//}
// Now output will be consistently 2000!




//Solution 2: Synchronized Block
//class Counter {
//    private int count = 0;
//    private Object lock = new Object();
//
//    public void increment() {
//        // Other code here (not synchronized)
//
//        synchronized (lock) {  // Synchronize only critical section
//            count++;
//        }
//
//        // Other code here
//    }
//}










//5️⃣ INTER-THREAD COMMUNICATION
//wait(), notify(), notifyAll()
//class SharedResource {
//    private int data;
//    private boolean hasData = false;
//
//    // Producer
//    public synchronized void produce(int value) throws InterruptedException {
//        while (hasData) {
//            wait();  // Wait until consumer consumes
//        }
//
//        this.data = value;
//        this.hasData = true;
//        System.out.println("Produced: " + value);
//        notify();  // Notify consumer
//    }
//
//    // Consumer
//    public synchronized int consume() throws InterruptedException {
//        while (!hasData) {
//            wait();  // Wait until producer produces
//        }
//
//        int value = this.data;
//        this.hasData = false;
//        System.out.println("Consumed: " + value);
//        notify();  // Notify producer
//        return value;
//    }
//}
//// Producer thread
//Thread producer = new Thread(() -> {
//    try {
//        for (int i = 1; i <= 5; i++) {
//            resource.produce(i);
//        }
//    } catch (InterruptedException e) {
//        e.printStackTrace();
//    }
//});
//// Consumer thread
//Thread consumer = new Thread(() -> {
//    try {
//        for (int i = 1; i <= 5; i++) {
//            resource.consume();
//        }
//    } catch (InterruptedException e) {
//        e.printStackTrace();
//    }
//});
//producer.start();
//consumer.start();
//Output:
//
//Produced: 1
//Consumed: 1
//Produced: 2
//Consumed: 2
//
//        ...