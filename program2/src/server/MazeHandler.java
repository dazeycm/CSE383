//OKAY TO USE X FOR WALL WHEN TELLING USER?
//QUIT WITHOUT CLOSE? QUIT WITH CLOSE?
//HAS TO BE RANDOM 

package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MazeHandler {

	public final static int MAZESIZE = 9;
	
	public static char[][] maze = new char[MAZESIZE][MAZESIZE];
	public static boolean mazeInit = false;
	public static Map<Integer, User> sid = new HashMap<Integer, User>();
	public static int currId = -1;
	private static int startX = 0;
	private static int startY = 0;
	private static DB db = new DB();
	
	public String connect(String user, String pass) {
		createMazeArray();
		
		for(Map.Entry<Integer, User> entry : sid.entrySet()) {
			if (entry.getValue().getUsername().equals(user)) {
				return "-1"; // return -1 to represent that username already exists
			}
		}
		
		currId++;
		try {
			db.addUser(user, startX, startY);
		} catch (SQLException e) {
			return "-1";
		}
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
		String user = this.sid.get(sid).getUsername();
		
		try {
			db.getXY(user);
		} catch (SQLException e) {
			return "-1";
		}
		return sid;
	}
	
	public String move(String username, String direction) {
		return username;
	}
	
	public String get() {
		return null;
	}
	
	private void createMazeArray() {
		if(mazeInit)
			return;	//check to see if array has already been initialized
			
		try {
			BufferedReader in = new BufferedReader(new FileReader("maze.in"));
			int row = 0;
			int column = 0;
			
			while (in.ready()) { 
				  String text = in.readLine();
				  for(char c : text.toCharArray()) {
					  if(c == 'S') {
						  startX = column;
						  startY = row;
					  }
					  maze[row][column] = c;
					  if(column + 1 > MAZESIZE - 1)
						  column = 0;
					  else
						  column++;
				  }
				  row++;
				}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("File with maze could not be found");
		} catch (IOException e) {
			System.err.println("Could not read from file");
		}
		
		System.out.println("Maze Created");
		
		mazeInit = true;
	}
	
	private char[] getSurrounding(int x, int y) {
		char[] ret = new char[4];
		
		//north
		if(y - 1 < 0)
			ret[0] = 'X';
		else
			ret[0] = maze[y - 1][x];
		
		//east
		if(x + 1 > MAZESIZE - 1)
			ret[1] = 'X';
		else 
			ret[1] = maze[y][x + 1];
		
		//south
		if(y + 1  > MAZESIZE - 1)
			ret[2] = 'X';
		else
			ret[2] = maze[y + 1][x];
		
		//west
		if(x - 1 < 0)
			ret[3] = 'X';
		else 
			ret[3] = maze[y][x - 1];
		
		return ret;
	}
}
