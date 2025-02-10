package Server;


import utils.Config;
import utils.MatrixUtils;
import utils.Request;
import utils.SlaveServerInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class OperationsImpl extends UnicastRemoteObject implements Operations {
    private Map<String , SlaveServerInfo > serverInfoMap ;
    private static final String[] SLAVES = new String[] { "00" , "01" , "10" , "11"} ;
    public OperationsImpl() throws RemoteException {
        Config config = new Config() ;
        config.loadConfig(Server.configFilePath);

        this.serverInfoMap = config.getSlaveServerInfoMap() ;
    }
    private int[][][] sendRequest(int[][][] slaveA ,  int[][][] slaveB , char operator) {
        int[][][] resultSlaves = new int[4][][] ;
        Thread[] threads = new Thread[4] ;

        for (int i = 0; i < 4 ; i++) {
            // Create a new thread for each slave with its own Socket
            threads[i] = new Thread(
                    new SlaveRequestHandler(
                            this.serverInfoMap.get("server"+i).getHostName() ,
                            this.serverInfoMap.get("server"+i).getPort() ,
                            new Request(SLAVES[i], operator, slaveA[i], slaveB[i]),
                            resultSlaves
                    )
            );
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();  // to ensure that the threads finished his processing
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return resultSlaves ;
    }
    public int[][][] deviseMatrix(int[][] A) {
        int longeur = A.length ;
        int largeur = A[0].length ;
        int[][] slave00 = MatrixUtils.slice(A , 0 , 0 , longeur / 2 , largeur / 2) ;
        int[][] slave01 = MatrixUtils.slice(A , 0 , largeur/2 , longeur / 2 , largeur ) ;
        int[][] slave10 = MatrixUtils.slice(A , longeur/2 , 0 , longeur , largeur/2) ;
        int[][] slave11 = MatrixUtils.slice(A , longeur / 2 , largeur /2 , longeur , largeur) ;
        return new int[][][] { slave00 , slave01 , slave10 , slave11 } ;
    }
    // to convert a response row to int array
    @Override
    public int[][] add(int[][] A, int[][] B ) throws RemoteException {
        if(MatrixUtils.verifyDimension(A , B )) {
            // centralize the matrices to one matrix
            int[][][] slaveA = deviseMatrix(A);
            int[][][] slaveB = deviseMatrix(B);

            int[][][] resultSlaves = sendRequest(slaveA , slaveB , '+') ;
            // merge the slaves result and return them to client
            int[][] result = MatrixUtils.merge(resultSlaves[0] , resultSlaves[1] , resultSlaves[2] , resultSlaves[3] , A.length , A[0].length )  ;
            // remove the padding if we are applying a multiplication operation
            return result ;
        }else {
            throw new RemoteException("the matrix hasn't the same length") ;
        }
    }

    @Override
    public int[][] sub(int[][] A, int[][] B) throws RemoteException {
        if(MatrixUtils.verifyDimension(A , B )) {
            // centralize the matrices to one matrix
            int[][][] slaveA = deviseMatrix(A) ;
            int[][][] slaveB = deviseMatrix(B) ;

            int[][][] resultSlaves = sendRequest( slaveA , slaveB , '-') ;
            // merge the slaves result and return them to client
            int[][] result = MatrixUtils.merge(resultSlaves[0] , resultSlaves[1] , resultSlaves[2] , resultSlaves[3] , A.length , A[0].length )  ;
            // remove the padding if we are applying a multiplication operation
            return result ;
        }else {
            throw new RemoteException("the matrix hasn't the same length") ;
        }
    }

    @Override
    public int[][] multiply(int[][] A, int[][] B) throws RemoteException {
        if(A[0].length == B.length ) {
            int originalLngA = A.length ;
            int originalLrgB =  B[0].length ;
            A = MatrixUtils.matrix_padding(A) ;
            B = MatrixUtils.matrix_padding(B) ;
            int longeurA = A.length ;
            int largeurB = B[0].length ;
            // centralize the matrices to one matrix
            int[][][] slaveA = deviseMatrix(A) ;
            int[][][] slaveB = deviseMatrix(B);

            int[][][] resultSlaves = sendRequest(slaveA , slaveB , '*') ;

            // merge the slaves result and return them to client
            int[][] result = MatrixUtils.merge(resultSlaves[0] , resultSlaves[1] , resultSlaves[2] , resultSlaves[3] , longeurA , largeurB )  ;
            // remove the padding if we are applying a multiplication operation
            int[][] croppedResult = new int[originalLngA][originalLrgB];
            for (int i = 0; i < originalLngA; i++) {
                for (int j = 0; j < originalLrgB; j++) {
                    croppedResult[i][j] = result[i][j];
                }
            }
            return croppedResult;
        }else {
            throw new RemoteException("Matrix dimensions are incompatible for multiplication.");
        }

    }
}
