package Server;

import ressources.Config;

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

public class Server {
    public static String configFilePath  ;

    public static void main(String[] args) {
        if(args.length >= 1 ) {
            configFilePath = args[0] ;
            Config config = new Config() ;
            config.loadConfig(configFilePath) ;
            int port = config.getRmiServerPort()  ;
            String hostName = config.getRmiServerHostName() ;

            Operations operations;
            try {
                operations = new OperationsImpl();

                LocateRegistry.createRegistry(port) ;

                System.out.println(Date.from(Instant.now()) + " the rmi server started");
                Naming.rebind(String.format("rmi://%s:%d/compute" , hostName , port )  , operations);

            } catch(RemoteException e){
                throw new RuntimeException(e);
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("invalid arguments please enter the application properties file path");
        }

    }
}
