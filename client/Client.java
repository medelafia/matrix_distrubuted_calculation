package client;

import Server.Operations;
import ressources.Config;
import utils.MatrixUtils;
import utils.Request;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.time.Instant ;

public class Client {

    public static void main(String[] args) {
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

            try {
                int[][] res = operations.compute(request.getA() , request.getB() , request.getOperator()) ;
                System.out.println(Date.from(Instant.now()) + " The result is:");
                System.out.println(MatrixUtils.matToString(res));
            }catch (Exception exception){
                exception.printStackTrace();
            }
        } catch (NotBoundException e) {
            e.printStackTrace( );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
