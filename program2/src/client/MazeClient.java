package client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.*;
import java.util.Scanner;
import java.io.*;

public class MazeClient {
	XmlRpcClient xmlRPCclient = null;
	int port = -1;
	String id = "";
	String username;
	String password;
	
	public MazeClient(String host,int p) throws IOException {
		port = p;
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://" + host+":"+port));
		xmlRPCclient = new XmlRpcClient();
		xmlRPCclient.setConfig(config);
	}
	
	public void run() {
		try{
			Scanner kb = new Scanner(System.in);
			connect();
			String next;
			
			String looks = look();
			if(looks.equals("-1")) {
				System.err.println("Error from look function");
				throw new XmlRpcException("Sid was not valid or state was not 'active'");
			}
			System.out.println(looks);
			
			while(true) {
				printCommands();
				next = kb.nextLine();
				switch(next) {
				case "N":
				case "S":
				case "W":
				case "E":
					String moves = move(next);
					if(moves.equals("DONE")) {
						String ret = close(password);
						if(ret.equals("OK")) {
							System.out.println("Congratulations, you finished!");
							System.exit(0);
						} else {
							System.err.println(ret);
						}
					}
					else if(moves.equals("DIED")) {
						String ret = close(password);
						if(ret.equals("OK")) {
							System.out.println("Congratulations, you died!");
							System.exit(0);
						} else {
							System.err.println(ret);
						}
					}
					else if(moves.equals("-1")) {
						System.err.println("Error from move function");
						throw new XmlRpcException("Sid was not valid or state was not 'active'");
					}
					else if(moves.equals("-2"))
						System.err.println("Invalid move");
					else if(moves.equals("OK")){
						looks = look();
						if(looks.equals("-1")) {
							System.err.println("Error from look function");
							throw new XmlRpcException("Sid was not valid or state was not 'active'");
						}
						System.out.println(looks);
					}
					break;
				case "G":
					get();
					break;
				case "Q":
				case "C":
					close(password);
					break;
				}
			}
		} 
		catch(Exception err) {
			System.err.println("Error in main run loop: " + err);
			return;
		}
	}
	
	public void connect() throws XmlRpcException  {
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Username: ");
		username = kb.next();
		
		System.out.print("Password: ");
		password = kb.next();
		
		id = connectToServer(username, password);
		if (id.equals("-1")) {
			kb.close();
			throw new XmlRpcException("Failed to add user to db, username may already exist");
		}
	}
	
	public void printCommands() {
		System.out.print("N|E|S|W to move\n");
		System.out.print("G to get\n");
		System.out.print("Q to quit\n");
		System.out.print("C to quit\n\n\n");
	}
	
	public String move(String direction) throws XmlRpcException {
		Object[] params = new Object[]{id, direction};
		String result= (String) xmlRPCclient.execute("mazehandler.move", params);
		return result;
	}
	
	public String get() throws XmlRpcException {
		Object[] params = new Object[]{};
		String result= (String) xmlRPCclient.execute("mazehandler.get", params);
		return result;
	}
	
	public String look() throws XmlRpcException {
		Object[] params = new Object[]{id};
		String result= (String) xmlRPCclient.execute("mazehandler.look", params);
		return result;
	}
	
	public String connectToServer(String user, String pass) throws XmlRpcException {
		Object[] params = new Object[]{user, pass};
		String result= (String) xmlRPCclient.execute("mazehandler.connect", params);
		return result;
	}
	
	public String close(String pass) throws XmlRpcException {
		Object[] params = new Object[]{id, pass};
		String result= (String) xmlRPCclient.execute("mazehandler.close", params);
		return result;
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage:  java AddMessage <HOST> <PORT>");
		}
		
		int port = -1;
		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception err) {
			System.out.println("specify port");
			return;
		}

		try {
			MazeClient client = new MazeClient(args[0],port);
			client.run();
		}
		catch (Exception err) {
			System.err.println("Error initializing client: " + err);
			return;
		}
	}
	
}
