import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerRemote extends Remote{
    public void election(int senderID) throws RemoteException;
    public void ok(int senderID) throws RemoteException;
    public void electLeader(int leaderID) throws RemoteException;
}