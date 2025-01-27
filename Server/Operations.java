package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Operations extends Remote {
    public int[][] sub(int[][] A , int[][] B) throws RemoteException ;
    public int[][] add(int[][] A , int[][] B ) throws RemoteException ;
    public int[][] multiply(int[][] A , int[][] B) throws RemoteException ;
}
