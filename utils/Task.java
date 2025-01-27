package utils;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class Task implements Serializable {
    private ObjectOutputStream objectOutputStream  ;
    private String taskId ;
    private Request request ;
    private CountDownLatch countDownLatch ;
    public Task(ObjectOutputStream objectOutputStream , Request request , CountDownLatch countDownLatch) {
        this.taskId = UUID.randomUUID().toString() ;
        this.objectOutputStream = objectOutputStream ;
        this.request = request ;
        this.countDownLatch = countDownLatch ;
    }

    public String getTaskId() {
        return taskId;
    }
    public void executeTask() {
        try {
            int[][] result = null ;
            switch (this.request.getOperator()) {
                case '*' :
                    result = MatrixUtils.multiplyTwoMatrix(this.request.getA() , this.request.getB() ) ;
                    break;
                case  '+' :
                    result = MatrixUtils.addTwoMatrix(this.request.getA() , this.request.getB() ) ;
                    break;
                case '-' :
                    result = MatrixUtils.subTwoMatrix(this.request.getA() , this.request.getB() ) ;
                    break ;
            }
            Response response = new Response(this.request.getSlaveId() , result );
            this.objectOutputStream.flush();
            this.objectOutputStream.writeObject(response);
        }catch (Exception e) {
            System.err.println(Date.from(Instant.now()) + "Error executing task: " + e.getMessage());
        }finally {
            // to inform that the task is finished processing
            countDownLatch.countDown();
        }

    }
}
