package Server;


import ressources.Config;
import utils.MatrixUtils;
import utils.Request;
import utils.SlaveServerInfo;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class OperationsImpl extends UnicastRemoteObject implements Operations {
    private Map<String , SlaveServerInfo > serverInfoMap ;
    public OperationsImpl() throws RemoteException {
        Config config = new Config() ;
        config.loadConfig(Server.configFilePath);

        this.serverInfoMap = config.getSlaveServerInfoMap() ;
    }
    // to convert a response row to int array
    @Override
    public int[][] compute(int[][] A, int[][] B , char operator) throws RemoteException {
        int longeur = A.length ;
        int largeur = A[0].length ;
        int originalLng = A.length ;
        int originalLrg =  A[0].length ;
        if(MatrixUtils.verifyDimension(A , B ) ) {
            if(operator == '*') {
                A = MatrixUtils.matrix_padding(A) ;
                B = MatrixUtils.matrix_padding(B) ;
                longeur = A.length ;
                largeur = A[0].length ;
            }
            // slice the matrix to 4 slave
            int[][] slaveA00 = MatrixUtils.slice(A , 0 , 0 , longeur / 2 , largeur / 2) ;
            int[][] slaveA01 = MatrixUtils.slice(A , 0 , largeur/2 , longeur / 2 , largeur ) ;
            int[][] slaveA10 = MatrixUtils.slice(A , longeur/2 , 0 , longeur , largeur/2) ;
            int[][] slaveA11 = MatrixUtils.slice(A , longeur / 2 , largeur /2 , longeur , largeur) ;
            // slice the matrix b to 4 slaves
            int[][] slaveB00 = MatrixUtils.slice(B , 0 , 0 , longeur / 2 , largeur / 2) ;
            int[][] slaveB01 = MatrixUtils.slice(B , 0 , largeur/2 , longeur / 2 , largeur ) ;
            int[][] slaveB10 = MatrixUtils.slice(B , longeur/2 , 0 , longeur , largeur/2) ;
            int[][] slaveB11 = MatrixUtils.slice(B , longeur / 2 , largeur /2 , longeur , largeur) ;
            // centralize the matrices to one matrix
            int[][][] slaveA = { slaveA00 , slaveA01 , slaveA10 , slaveA11 } ;
            int[][][] slaveB = { slaveB00 , slaveB01 , slaveB10 , slaveB11 } ;
            // slaves id to identify which slave part after processing
            String[] slaves = new String[] { "00" , "01" , "10" , "11"} ;

            int[][][] resultSlaves = new int[4][][] ;
            Thread[] threads = new Thread[slaves.length] ;

            for (int i = 0; i < slaves.length; i++) {
                    // Create a new thread for each slave with its own Socket
                    threads[i] = new Thread(
                            new SlaveRequestHandler(
                                    this.serverInfoMap.get("server0").getHostName() ,
                                    this.serverInfoMap.get("server0").getPort() ,
                                    new Request(slaves[i], operator, longeur, largeur, slaveA[i], slaveB[i]),
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
            // merge the slaves result and return them to client
            int[][] result = MatrixUtils.merge(resultSlaves[0] , resultSlaves[1] , resultSlaves[2] , resultSlaves[3] , longeur , largeur )  ;
            // remove the padding if we are applying a multiplication operation
            if (operator == '*') {
                int[][] croppedResult = new int[originalLng][originalLrg];
                for (int i = 0; i < originalLng; i++) {
                    for (int j = 0; j < originalLrg; j++) {
                        croppedResult[i][j] = result[i][j];
                    }
                }
                return croppedResult;
            }
            return result ;
        }else {
            throw new RemoteException("the matrix hasn't the same length") ;
        }
    }
}
