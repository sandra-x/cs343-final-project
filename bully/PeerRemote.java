import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerRemote extends Remote{
    public void election() throws RemoteException;

}