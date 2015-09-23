package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MazeHandler {

	public final static int MAZESIZE = 9;
	
	public static char[][] maze = new char[MAZESIZE][MAZESIZE];
	public static boolean mazeInit = false;
	public static Map<Integer, User> sid = new HashMap<Integer, User>();
	public static int currId = -1;
	
	public String connect(String user, String pass) {
		for(Map.Entry<Integer, User> entry : sid.entrySet()) {
			if (entry.getValue().getUsername().equals(user)) {
				return "-1"; // return -1 to represent that username already exists
			}
		}
		currId++;
		sid.put(currId, new User(user, pass));
		return Integer.toString(currId);
	}
	
	public String close(String sid, String pass) {
		//need to set in db to done
		
		if(!MazeHandler.sid.containsKey(sid)) {
			return "SID not found";
		}
		
		if(!MazeHandler.sid.get(sid).getPassword().equals(pass)) {
			return "Password not found";
		}
		
		return "OK";
	}
	
	public String look(String sid) {
		return sid;
	}
	
	public String move(String username, String direction) {
		return username;
	}
	
	public String get() {
		return null;
	}
	
	public void createMazeArray() {
		
		if(mazeInit)
			return;	//check to see if array has already been initialized
			
		try {
			BufferedReader in = new BufferedReader(new FileReader("maze.in"));
			int row = 0;
			int column = 0;
			
			while (in.ready()) { 
				  String text = in.readLine();
				  for(char c : text.toCharArray()) {
					  maze[row][column] = c;
					  if(column + 1 > MAZESIZE - 1)
						  column = 0;
					  else
						  column++;
				  }
				  row++;
				}
		} catch (FileNotFoundException e) {
			System.err.println("File with maze could not be found");
		} catch (IOException e) {
			System.err.println("Could not read from file");
		}
		
		mazeInit = true;
	}
}
