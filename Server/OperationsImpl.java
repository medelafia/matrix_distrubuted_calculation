package Server;


import ressources.Config;
import utils.MatrixUtils;
import utils.Request;
import utils.SlaveServerInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class OperationsImpl extends UnicastRemoteObject implements Operations {
    private Map<String , SlaveServerInfo > serverInfoMap ;
    public OperationsImpl() throws RemoteException {
        Config config = new Config() ;
        config.loadConfig(Server.configFilePath);

        this.serverInfoMap = config.getSlaveServerInfoMap() ;
    }
    // to convert a response row to int array
    @Override
    public int[][] add(int[][] A, int[][] B ) throws RemoteException {
        int longeur = A.length ;
        int largeur = A[0].length ;
        if(MatrixUtils.verifyDimension(A , B )) {
            A = MatrixUtils.matrix_padding(A) ;
            B = MatrixUtils.matrix_padding(B) ;
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
                                    new Request(slaves[i], '+', slaveA[i], slaveB[i]),
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
            return result ;
        }else {
            throw new RemoteException("the matrix hasn't the same length") ;
        }
    }

    @Override
    public int[][] sub(int[][] A, int[][] B) throws RemoteException {
        int longeur = A.length ;
        int largeur = A[0].length ;
        if(MatrixUtils.verifyDimension(A , B )) {
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
                                new Request(slaves[i], '-', slaveA[i], slaveB[i]),
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
            return result ;
        }else {
            throw new RemoteException("the matrix hasn't the same length") ;
        }
    }

    @Override
    public int[][] multiply(int[][] A, int[][] B) throws RemoteException {
        if(A[0].length == B.length ) {
            int originalLngA = A.length ;
            int originalLrgA =  A[0].length ;
            int originalLngB = B.length ;
            int originalLrgB =  B[0].length ;
            A = MatrixUtils.matrix_padding(A) ;
            B = MatrixUtils.matrix_padding(B) ;
            int longeurA = A.length ;
            int largeurA = A[0].length ;
            int longeurB = B.length ;
            int largeurB = B[0].length ;
            // slice the matrix to 4 slave
            int[][] slaveA00 = MatrixUtils.slice(A , 0 , 0 , longeurA / 2 , largeurA / 2) ;
            int[][] slaveA01 = MatrixUtils.slice(A , 0 , largeurA/2 , longeurA / 2 , largeurA ) ;
            int[][] slaveA10 = MatrixUtils.slice(A , longeurA/2 , 0 , longeurA , largeurA/2) ;
            int[][] slaveA11 = MatrixUtils.slice(A , longeurA / 2 , largeurA /2 , longeurA , largeurA) ;
            // slice the matrix b to 4 slaves
            int[][] slaveB00 = MatrixUtils.slice(B , 0 , 0 , longeurB / 2 , largeurB / 2) ;
            int[][] slaveB01 = MatrixUtils.slice(B , 0 , largeurB/2 , longeurB / 2 , largeurB ) ;
            int[][] slaveB10 = MatrixUtils.slice(B , longeurB/2 , 0 , longeurB , largeurB/2) ;
            int[][] slaveB11 = MatrixUtils.slice(B , longeurB / 2 , largeurB /2 , longeurB , largeurB) ;
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
                                new Request(slaves[i], '*' , slaveA[i], slaveB[i]),
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
