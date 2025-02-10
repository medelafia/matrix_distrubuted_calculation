package client;

import Server.Operations;
import utils.Config;
import utils.MatrixUtils;
import utils.Request;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    public static void main(String[] args) {
        if(args.length == 3) {
            String configFilePath = args[1] ;
            String dataFilePath = args[0] ;


            Config config = new Config() ;
            config.loadConfig(configFilePath);
            int port = config.getRmiServerPort()  ;
            String hostName = config.getRmiServerHostName() ;

            System.out.flush();

            Request request = Request.loadRequestFromFile(dataFilePath) ;

            Operations operations = null;
            try {
                operations = (Operations) Naming.lookup(String.format( "rmi://%s:%d/compute", hostName , port ) );

                int[][] res = null ;
                switch (request.getOperator() ) {
                    case '*' :  res = operations.multiply(request.getA() , request.getB() ) ;
                        break;
                    case '-' : res = operations.add(request.getA() , request.getB() ) ;
                        break;
                    case '+' : res = operations.add(request.getA() , request.getB() ) ;
                        break;
                }
                try(FileWriter fileWriter = new FileWriter(args[2])) {
                    fileWriter.write(MatrixUtils.matToString(res));
                    System.out.println("the operation was successful check the output file");
                }catch (IOException e ) {
                    System.out.println(e.getMessage());
                }

            } catch (NotBoundException e) {
                System.out.println(e.getMessage());
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        }else {
            System.out.println("invalid arguments ");
            System.out.println("USAGE : java -jar Client.jar <matrix_file> <configuration_file> <output_file>");
        }
    }
}
