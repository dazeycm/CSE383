package server;

import java.sql.*;

public class DB {
	private Connection connection;
	
	private String user = "mazeuser";
	private String pwd = "maze";
	private Statement stmnt;
	private String dbURL = "jdbc:mysql://localhost/mazedata";
	private Connection conn = null;

	
	public DB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, user, pwd);
			stmnt = conn.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			System.err.println("Failed to connect to database: " + e);
		}
	}
	
	public void addUser(String name, int x, int y) throws SQLException {
		String sql = "INSERT INTO maze VALUES(" + "\"" + name + "\", "
												+ x + ", "
												+ y + ", "
												+ System.currentTimeMillis() / 1000L + ", "
												+ 0 + ", "
												+ "'ACTIVE')";			
		stmnt.executeUpdate(sql);
	}
	
	public int[] getXY(String user) throws SQLException {
		String sql = "SELECT x, y, state FROM maze WHERE name = " + "\"" + user + "\"";
		ResultSet rs = stmnt.executeQuery(sql);
		rs.next();
		if(!rs.getString("state").equals("ACTIVE")) {
			throw new SQLException("User state was not active");
		}
		return new int[] {rs.getInt("X"), rs.getInt("Y")};
	}
	
	public void move(String user, int[] direction) throws SQLException {
		String sql = "UPDATE maze SET X = " + direction[0] + ", Y = " + direction[1] + " WHERE name = " + "\"" + user + "\"";
		stmnt.executeUpdate(sql);
		sql = "UPDATE maze SET moves = moves + 1 WHERE name = " + "\"" + user + "\"";
		stmnt.executeUpdate(sql);
	}
	
	public void changeToFinished(String user) throws SQLException {
		String sql = "UPDATE maze SET state = 'FINISHED' WHERE name = " + "\"" + user + "\"";
		stmnt.executeUpdate(sql);
	}
	
	public void deleteUser(String user) throws SQLException {
		String sql = "DELETE FROM maze WHERE name = " + "\"" + user + "\"";
		stmnt.executeUpdate(sql);
	}
	
	public ResultSet getAllInfo() throws SQLException {
		String sql = "SELECT * FROM maze";
		ResultSet rs = stmnt.executeQuery(sql);
		return rs;
	}
	
}
