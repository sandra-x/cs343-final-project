import java.io.IOException;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileInputStream;

class Peer implements PeerRemote
{
   private String userName;
   private String ipAddress;
   public int myID;
   public String successor;
   public String successorIP;
   public int leaderID;

   /* Constructor that takes in username, ipAdress,
   */
   public Peer(String userName, String ipAddress, int processNumber, String neighbor, String neighborIP)
   {
      this.userName = userName;
      this.ipAddress = ipAddress;
      this.myID = processNumber;
      this.successor = neighbor;
      this.successorIP = neighborIP;

      this.leaderID = 0;
   }

   /* Hi
   */
   public void election(int highID) {
     try {
       System.err.println("Current Highest ID: " + highID);

       if (highID > this.myID){
         System.err.println("My ID " + this.myID + " is NOT higher. Pass on ID " + highID + " to " + this.successor);
         Registry registry = LocateRegistry.getRegistry(this.successorIP); //get registry of the IP of neighbor
         PeerRemote stubN = (PeerRemote) registry.lookup("peer");
         stubN.election(highID);
       } else if (this.myID > highID){
         System.err.println("My ID "+ this.myID +" is higher! Pass it on to " + this.successor);
         Registry registry = LocateRegistry.getRegistry(this.successorIP); //get registry of the IP of neighbor
         PeerRemote stubN = (PeerRemote) registry.lookup("peer");
         stubN.election(this.myID);
       } else if (highID == this.myID){
         this.leaderID = this.myID;
         System.err.println("This is MY ID: " + this.myID + " which makes me the leader!");
         Registry registry = LocateRegistry.getRegistry(this.successorIP); //get registry of the IP of neighbor
         PeerRemote stubN = (PeerRemote) registry.lookup("peer");
         stubN.leader(this.myID);
       }

     } catch (Exception e){
       System.err.println("Election message exception:" + e.toString());
       e.printStackTrace();
     }
   } //end of query method definition

    /* hi
   */
   public void leader(int highestID){
     try{
       this.leaderID = highestID;
       System.err.println("Hey! The elected leader with the highest ID is " + highestID + " Pass message to " + this.successor);
       if (this.myID != highestID){
         Registry registry = LocateRegistry.getRegistry(this.successorIP); //put ip
         PeerRemote stubBack = (PeerRemote) registry.lookup("peer");
         stubBack.leader(highestID);
       }
       System.err.println("Both the election and leader messages have completed.");

   } catch (Exception e){
       System.err.println("Leader message exception:" + e.toString());
       e.printStackTrace();
     }
   }

   public static void main(String[] args) {
     try{
       if(args.length < 1) {
          System.err.println("Error, usage: java ClassName inputfile");
  	      System.exit(1);
        }

       // read input file which contains peer's name, ip, username, neighbors' names and ips, and own files
       Scanner reader = new Scanner(new FileInputStream(args[0]));
       Scanner readerCommand = new Scanner(System.in);
       //String fromText = "";
       String username = "";
       String IPAddress = "";
       String processIDstring = "";
       String successorInfo = "";
       String successor = "";
       String successorIP = "";

       username = reader.nextLine(); //processName
       IPAddress = reader.nextLine();
       processIDstring = reader.nextLine(); // needs to be converted into int
       successorInfo = reader.nextLine();
       successor = successorInfo.split(" ")[0];
       successorIP = successorInfo.split(" ")[1];

       int processNumber = Integer.parseInt(processIDstring);

       // create a peer object and stub and add to registry
       Peer client1 = new Peer(username, IPAddress, processNumber, successor, successorIP);
       PeerRemote stub = (PeerRemote) UnicastRemoteObject.exportObject(client1, 0);
       Registry registry = LocateRegistry.getRegistry();
       registry.bind("peer", stub);
       System.err.println("Ready...");

      // read query inputs and set variables for remembering the song name
      while (readerCommand.hasNextLine()){
        String command = readerCommand.nextLine();
        System.err.println("Your command: " + command); // Your command: start
        if(command.startsWith("start")){
          client1.election(0); //default of highest ID is 0
       }
     }

     } catch (Exception e) {
       System.err.println("Peer exception: " + e.toString());
       e.printStackTrace();
     }
   }


}