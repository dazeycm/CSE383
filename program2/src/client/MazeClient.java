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
	
	public MazeClient(String host,int p) throws IOException {
		port = p;
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://" + host+":"+port));
		xmlRPCclient = new XmlRpcClient();
		xmlRPCclient.setConfig(config);
	}
	
	public void run() {
		try{
			connect();
			
			while(true) {
				
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
		String username = kb.next();
		
		System.out.println("Password: ");
		String password = kb.next();
		
		id = connectToServer(username, password);
		if (id.equals("-1")) {
			kb.close();
			throw new XmlRpcException("Username is already in use");
		}
		
		kb.close();
	}
	
	public String connectToServer(String user, String pass) throws XmlRpcException {
		Object[] params = new Object[]{user, pass};
		String result= (String) xmlRPCclient.execute("mazehandler.connect", params);
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
