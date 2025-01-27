package slaves;

import utils.Request;
import utils.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class ClientHandler implements Runnable {

    private ServerSocket serverSocket ;
    private CountDownLatch countDownLatch ;

    public ClientHandler(ServerSocket socket) {
        this.serverSocket = socket ;
        this.countDownLatch = new CountDownLatch( 1 ) ;
    }
    @Override
    public void run() {
        try(Socket socket = serverSocket.accept() ;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()) ;
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            objectOutputStream.flush();
            Request request = (Request)objectInputStream.readObject() ;
            System.out.println(Date.from(Instant.now()) + "new request received from " +
                    socket.getRemoteSocketAddress() + ", slave id" +
                    request.getSlaveId() + ", operation " + request.getOperator()  );


            Task task = new Task(objectOutputStream , request , countDownLatch ) ;
            SlaveServer.tasksQueue.put(task);

            countDownLatch.await();
        } catch (IOException e) {
            System.out.println(Date.from(Instant.now()) + "error in object input or output stream connection");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
