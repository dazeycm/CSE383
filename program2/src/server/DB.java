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
	
	public void getXY(String user) throws SQLException {
		String sql = "SELECT x, y FROM maze WHERE name = " + "\"" + user + "\"";
		ResultSet rs = stmnt.executeQuery(sql);
		System.out.println(rs.toString());
	}
	
}
