import java.io.IOException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileInputStream;
import java.lang.Thread;

class Peer implements PeerRemote {

    private String username;
    private int ID;
    public String leader;
    public int leaderID;
    private boolean receivedOK = false;
    private boolean receivedLeader = false;
    public int waitTimeSeconds = 4;

    // process ids and ips
    private HashMap<Integer, String> neighborIPs = new HashMap<Integer, String>(){
        private static final long serialVersionUID = 1L; //?? vscode made me do this
        {
        put(7, "172.31.85.216");
        put(2, "172.31.85.48");
        put(5, "172.31.45.218");
        put(8, "172.31.34.69");
        put(6, "172.31.36.251");

        put(11, "110.10.10.01"); //failed process

    }};

    // process usernames and ids
    private HashMap<String, Integer> neighborIDs = new HashMap<String, Integer>(){
        private static final long serialVersionUID = 1L;
        {
        put("A", 7);
        put("B", 2);
        put("C", 5);
        put("D", 8);
        put("E", 6);
        // put("F", 6);
        put("G", 11); // failed process
    }};


    public Peer (String username) {

        this.username = username;
        // this.ID = int(this.username);
        this.ID = this.neighborIDs.get(this.username);
    }

    // send election message to all neighbors with higher ID
    private void sendElection(int myID) {
        for (Map.Entry<Integer, String> entry: this.neighborIPs.entrySet()) {
            if (!receivedLeader) {
                if (entry.getKey() > myID) {
                    try{
                        System.out.println("sending election to process " + entry.getKey()); // do username later
                        Registry registry = LocateRegistry.getRegistry(entry.getValue()); //get registry of the IP of neighbor
                        PeerRemote sendTo = (PeerRemote) registry.lookup("peer");
                        sendTo.election(this.ID);
                    } catch (Exception e) {
                        System.err.println("Election message exception:" + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        }
        try{
            Thread.sleep(waitTimeSeconds*1000);
            // if haven't received ok message, current peer is the new leader
            if (!this.receivedOK) {
                System.out.println("I am the new leader");
                // send leader message to all peers with higher ID
                for (Map.Entry<Integer, String> entry: this.neighborIPs.entrySet()) {
                    if (entry.getKey() < myID) {
                        try{
                            System.out.println("sending leaader to process " + entry.getKey()); // do username later
                            Registry registry = LocateRegistry.getRegistry(entry.getValue()); //get registry of the IP of neighbor
                            PeerRemote sendTo = (PeerRemote) registry.lookup("peer");
                            sendTo.leader(this.ID);
                        } catch (Exception e) {
                            System.err.println("Election message exception:" + e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // receive an election message, 
    public void election(int senderID) {
        try {
            //Immediately sends Coordinator message if it is the process with highest ID

            System.out.println("received election message from process " + senderID);
            //check if current peer ID is greater than senderID. if it is, send an OK message back
            if (this.ID > senderID){
                Registry registry = LocateRegistry.getRegistry(this.neighborIPs.get(senderID)); //get registry of the IP of neighbor
                PeerRemote sender = (PeerRemote) registry.lookup("peer");
                sender.ok(this.ID);
                sendElection(this.ID);          
            }
     
        } catch (Exception e){
            System.err.println("Election message exception:" + e.toString());
            e.printStackTrace();
        }
    }

    // if receive an ok message, drop out of election. 
    public void ok(int senderID){
        System.out.println("received ok from process " + senderID);
        this.receivedOK=true;
    }

    // receive message from new leader
    public void leader(int leaderID) {
        this.leaderID = leaderID;
        this.receivedLeader = true;
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
                    peer.sendElection(peer.ID);
                }
            }       
            scan.close();
            System.out.println("finished");

        } catch(Exception e) {
            System.err.println("Peer exception: " + e.toString());
            e.printStackTrace();
        }
    }

}