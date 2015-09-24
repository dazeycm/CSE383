//OKAY TO USE X FOR WALL WHEN TELLING USER?
//QUIT WITHOUT CLOSE? QUIT WITH CLOSE?
//HAS TO BE RANDOM SID?
//PRINT XML DOCUMENT?
//HANDLING ERRORS FROM SERVER TO CLIENT?

package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
		
		if(!MazeHandler.sid.containsKey(Integer.parseInt(sid))) {
			return "SID not found";
		}
		
		if(!MazeHandler.sid.get(Integer.parseInt(sid)).getPassword().equals(pass)) {
			return "Password not found";
		}
		
		return "OK";
	}
	
	public String look(String sid) {
		String user = MazeHandler.sid.get(Integer.parseInt(sid)).getUsername();
		int x, y;
		
		try {
			int[] cords = db.getXY(user);
			x = cords[0];
			y = cords[1];
		} catch (SQLException e) {
			return "-1";
		}
		
		StringBuilder sb = new StringBuilder();
		char[] surroundings = getSurrounding(x, y);
		
		for (char c : surroundings) {
			sb.append(c);
			sb.append(" ");
		}
		
		return sb.toString();
	}
	
	public String move(String sid, String direction) {
		String user = MazeHandler.sid.get(Integer.parseInt(sid)).getUsername();
		int[] directions = new int[2];
		int[] coords = new int[2];
		
		int[] cords = null;
		try {
			cords = db.getXY(user);
		} catch (SQLException e1) {
			return "-1";
		}
		
		int x = cords[0];
		int y = cords[1];
		switch(direction) {
		case "N":
			directions[0] = 0;
			directions[1] = -1;
			break;
		case "E":
			directions[0] = +1;
			directions[1] = 0;
			break;
		case "S":
			directions[0] = 0;
			directions[1] = +1;
			break;
		case "W": 
			directions[0] = -1;
			directions[1] = 0;
			break;
		}
		
		coords[0] = x + directions[0];
		coords[1] = y + directions[1];
		
		
		try{
			
			if(maze[coords[1]][coords[0]] == 'E') {
				db.changeToFinished(user);
				return "DONE";
			}
			if(maze[coords[1]][coords[0]] == 'P') {
				db.changeToFinished(user);
				return "DIED";
			}
			if(maze[coords[1]][coords[0]] != ' ')
				return "-2";		
		
		} catch (ArrayIndexOutOfBoundsException | SQLException e) {
			return "-2";
		}
		
		try {
			db.move(user, coords);
		} catch (SQLException e) {
			return "-1";
		}
		
		return "OK";
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
