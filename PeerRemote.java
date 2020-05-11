import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerRemote extends Remote {
    public void election(int highID) throws RemoteException;
    public void leader(int highestID) throws RemoteException;
}
