import java.io.IOException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileInputStream;

class Peer implements PeerRemote {

    private String username;
    private int ID;
    public String leader;
    public int leaderID;
    private boolean receivedOK = false;

    private HashMap<Integer, String> neighborIPs = new HashMap<Integer, String>(){
        private static final long serialVersionUID = 1L; //?? vscode made me do this

        {
        put(1, "172.31.42.207");
        put(2, "172.31.88.123");

        put(7, "123.21.24.214"); //failed process

    }};

    private HashMap<String, Integer> neighborIDs = new HashMap<String, Integer>(){
        private static final long serialVersionUID = 1L;
        {
        put("A", 1);
        put("B", 2);
        put("C", 3);
        put("D", 4);
        put("E", 5);
        put("F", 6);
        put("G", 7); // failed process
    }};


    public Peer (String username) {

        this.username = username;
        // this.ID = int(this.username);
        this.ID = this.neighborIDs.get(this.username);
    }

    // receive an election message, 
    public void election(int senderID) {
        try {
            //Immediately sends Coordinator message if it is the process with highest ID
     

            //check if current peer ID is greater than senderID. if it is, send an OK message back
            if (this.ID > senderID){
              Registry registry = LocateRegistry.getRegistry(this.neighborIPs.get(senderID)); //get registry of the IP of neighbor
              PeerRemote sender = (PeerRemote) registry.lookup("peer");
              sender.ok(this.ID);
            }
     
        } catch (Exception e){
            System.err.println("Election message exception:" + e.toString());
            e.printStackTrace();
        }
    }

    // if receive an ok message, drop out of election. 
    public void ok(int senderID){
        this.receivedOK=true;

    }

    // receive message from new leader
    public void electLeader(int leaderID) {
        this.leaderID = leaderID;

        //this.leader = this.neighborIDs.

        System.out.println( " is the new leader with Id " + this.leaderID);
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
            System.out.println("my username is " + peer.username + " and my ID is " + peer.ID);


            
            while(scan.hasNextLine()) {
                String command = scan.nextLine();
                // if peer says start election, call election method on all its neighbors
                if (command.startsWith("start")){
                    System.out.println("starting election");
                    
                }
            }
        
            //scan.close();

        } catch(Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }
    }

}