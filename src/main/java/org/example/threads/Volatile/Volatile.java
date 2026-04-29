package org.example.threads.Volatile;

class Volatile {
    private  boolean stop = false; /// here volatile is not there so while loop will ren forever

    public static void main(String[] args) throws InterruptedException {
        Volatile processor = new Volatile();
        processor.startProcessing();
        Thread.sleep(2000); /// 2 secs
        processor.stopProcessing();
    }

    private void stopProcessing() {
        stop = true;
    }

    private void startProcessing() {
        new Thread(() -> {
            System.out.println("Message processor started");

            while (!stop) {
                processMessage(); // takes ~1 sec
            }

            System.out.println("Message processor stopped");
        }).start();
    }

    private void processMessage() {
        // do work
    }
}