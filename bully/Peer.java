import java.io.IOException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileInputStream;

class Peer implements PeerRemote {

    private String username;
    private String myID;
    public String leader;
    public String leaderID;

    // private HashMap neighborIPs
    // private HasMap neighborIDs

    public Peer (String username) {
        this.username = username;

    }

    public void election() {

    }

    public static void main(String[] args) {

        try{
            Scanner scan = new Scanner(System.in);
            String username = scan.nextLine();

            Peer peer = new Peer(username);

            PeerRemote stub = (PeerRemote) UnicastRemoteObject.exportObject(peer, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("peer", stub);
            System.err.println("Ready...");
            System.out.println("my username is " + peer.username);


        
            //scan.close();

        } catch(Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }
    }

}