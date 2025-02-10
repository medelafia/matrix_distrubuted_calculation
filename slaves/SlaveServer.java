package slaves;


import utils.Config;
import utils.Task;
import utils.Worker;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.*;

public class SlaveServer {
    private static ServerSocket serverSocket ;
    public final static BlockingQueue<Task> tasksQueue = new ArrayBlockingQueue<>(10) ;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private static final ExecutorService clientHandlerService = Executors.newCachedThreadPool();
    public SlaveServer() throws IOException {
    }
    // main method to initialize the threads and make each server socket to a thread
    public static void main(String[] args) {
        if(args.length >= 3) {
            Config config = new Config() ;
            String configFilePath = args[0] ;
            config.loadConfig(configFilePath ) ;
            int serverNumber = Integer.parseInt( args[1] ) ;
            int numberOfWorkers = Integer.parseInt( args[2] ) ;
            try {
                String hostName = config.getSlaveServerInfoMap().get("server"+serverNumber).getHostName() ;
                int port = config.getSlaveServerInfoMap().get("server"+serverNumber).getPort() ;
                // starting the server
                System.out.println(Date.from(Instant.now()) + " slave server " + serverNumber + " server started now in " + hostName+":"+port) ;

                serverSocket = new ServerSocket(port , 50 , InetAddress.getByName(hostName));
                serverSocket.setReuseAddress(true);
                // execute workers
                for(int i = 0 ; i < numberOfWorkers ; i++ ) {
                    executorService.execute(new Worker("worker " + i , tasksQueue ));
                }
                // execute client handlers
                while (true) {
                    clientHandlerService.execute(new ClientHandler(serverSocket));
                }
            } catch (SocketException e) {
                System.out.println(Date.from(Instant.now()) + e.getMessage());
            } catch (IOException e) {
                System.out.println(Date.from(Instant.now()) + e.getMessage());
            } finally {
                Runtime.getRuntime().addShutdownHook(new Thread(()->{
                    try {
                        System.out.println(Date.from(Instant.now()) + "shutting down the workers");
                        if(executorService.awaitTermination(10 , TimeUnit.SECONDS)) {
                            executorService.shutdown();
                        }

                    }catch (InterruptedException interruptedException ) {
                        System.out.println(Date.from(Instant.now()) + " " + interruptedException.getMessage());
                        executorService.shutdownNow() ;
                    }
                }));
            }
        }else {
            System.out.println("invalid arguments");
        }


    }
}
