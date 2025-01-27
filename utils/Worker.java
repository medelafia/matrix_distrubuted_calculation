package utils;


import java.time.Instant;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    private String workerName ;
    private BlockingQueue<Task> taskQueue  ;
    public Worker(String workerName , BlockingQueue<Task> taskQueue) {
        this.workerName = workerName ;
        this.taskQueue = taskQueue ;
    }
    @Override
    public void run() {
        System.out.println(Date.from(Instant.now()) + " " + this.workerName +" started now");
        try {
            while(true) {
                Task task = this.taskQueue.take();

                System.out.println(Date.from(Instant.now()) + " running task " + task.getTaskId() + " executed now by " + this.workerName);
                task.executeTask();
                System.out.println(Date.from(Instant.now()) + "the task " + task.getTaskId() + " finished execution");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
