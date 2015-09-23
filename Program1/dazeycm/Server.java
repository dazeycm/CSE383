/*
   Craig Dazey
   CSE383-f15

   Assignment1

   UDP Messaging server
   ** A lot of the code from the lab file is reused **
   */

package dazeycm;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {

    DatagramSocket sock;
    int port;
    Log log;
    ArrayList<SocketAddress> listeners;
    
    public static void main(String[] args) {
	int port = 0;
	try {
	    port = Integer.parseInt(args[0]);
	} catch (Exception err) {
	    System.err.println("Could not parse arguemnt");
	    System.exit(-1);
	}
	
	new Server(port).run();
	
    }
    
    //Constructor for Server class
    public Server(int port) {
	this.port = port;
	try {
	    sock = new DatagramSocket(port);
	} catch (SocketException e) {
	    System.err.println("Could not establish DatagramSocket");
	    System.exit(-1);
	}
	
    }
    
    //Main run loop
    private void run() {
	log = new Log("server.log");
	listeners = new ArrayList<SocketAddress>(); //ArrayList to hold the addresses of all the clients
	log.log("Server has started on port " + port);
	
	while(true) {
	    try {
		//Continually receive UDP messages and act according to contents
		byte b[] = new byte[1024];
		DatagramPacket pkt = new DatagramPacket(b,b.length);
		sock.receive(pkt);
		ArrayList<String> msgs = getMessage(b);
		log.log("Got a packet from " + pkt.getSocketAddress());

		
		if (msgs.get(0).equals("HELLO")) {
		    //Add address to list
		    listeners.add(pkt.getSocketAddress());
		    log.log("Got hello from " + pkt.getSocketAddress());
		    sendMessage(pkt.getSocketAddress(), "HELLO-RESPONSE", "");
		} else if (msgs.get(0).equals("MESSAGE")) {
		    log.log("Got message from " + pkt.getSocketAddress() + ": " + msgs.get(1));
		    //Send message to every listening client
		    messageAll("MESSAGE", msgs.get(1), pkt.getSocketAddress());
		} else if (msgs.get(0).equals("GOODBYE")) {
		    //Remove address from list
		    listeners.remove(pkt.getSocketAddress());
		    log.log("Got quit from " + pkt.getSocketAddress());
		    sendMessage(pkt.getSocketAddress(), "GOODBYE-RESPONSE", "");
		}
		
	    } catch (IOException err) {
		log.log("Error during communication");
	    }
	}
	
    }
    
    //General method to receive a message, returns an arrayList of the received messages
    private ArrayList<String> getMessage(byte[] b) {
	try {
	    ArrayList<String> toRet = new ArrayList<String>();
	    ByteArrayInputStream bis= new ByteArrayInputStream(b);
	    DataInputStream dis = new DataInputStream(bis);
	    toRet.add(dis.readUTF());
	    toRet.add(dis.readUTF());
	    
	    return toRet;
	} catch (IOException err) {
	    log.log("Error receiving message");
	    return null;
	}
    }
    
    //Sends a message to all listening clients. Used to transmit messages to all parties
    private void messageAll(String msg1, String msg2, SocketAddress toSkip) {
	for(SocketAddress sa : listeners) {
	    if(!sa.equals(toSkip)) { // don't send message to client that originally sent it - this is a design decision of my choice 
		sendMessage(sa, msg1, msg2);
	    }
	}
    }
    
    //Send message to specified address
    private void sendMessage(SocketAddress sa, String msg1, String msg2) {
	try {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(bos);
	    dos.writeUTF(msg1);
	    dos.writeUTF(msg2);
	    byte data[] = bos.toByteArray();
	    DatagramPacket sendResponse = new DatagramPacket(data,data.length, sa);
	    sock.send(sendResponse);
	} catch (IOException err) {
	    log.log("Error sending message");
	}
    }
}
