package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Operations extends Remote {
    public int[][] compute(int[][] A , int[][] B , char operator) throws RemoteException ;
}
