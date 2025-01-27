package Server;

import utils.Request;
import utils.Response;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;

public class SlaveRequestHandler implements Runnable {
    public Socket socket ;
    private Request request  ;
    private int[][][] out ;
    public SlaveRequestHandler(String hostName , int port ,Request request , int out[][][]) {
        try {
            this.socket = new Socket(hostName, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.request = request ;
        this.out = out ;
    }
    @Override
    public void run() {
        System.out.println(Date.from(Instant.now()) + " the thread start now , to process slave "+this.request.getSlaveId() );
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()) ;
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        ){
            objectOutputStream.flush();

            System.out.println(Date.from(Instant.now()) + " request sent to slave server");
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            Response receivedMatrix = (Response) objectInputStream.readObject() ;
            System.out.println(Date.from(Instant.now()) +  " the slice "+ receivedMatrix.getSlaveId() +" arrived");
            synchronized (out) {
                switch (receivedMatrix.getSlaveId()) {
                    case "00" : out[0] = receivedMatrix.getResult() ;
                        break ;
                    case "01" : out[1] = receivedMatrix.getResult()  ;
                        break;
                    case "10" : out[2] = receivedMatrix.getResult()  ;
                        break;
                    case "11" : out[3] = receivedMatrix.getResult()  ;
                        break;
                }
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace( );
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
