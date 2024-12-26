package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class ConnectSQL {
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	private final String HOST = "localhost:3306";
	private final String DATABASE = "kingshcut";
	private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);
	
	public Connection conn;
	private Statement stmt;
	public static ConnectSQL connect = ConnectSQL.getInstance();
	
	// bakal nyetor value
	public ResultSet rs;
	
	public ResultSetMetaData rsm;
	
	//Singleton
	public static ConnectSQL getInstance() {
		if(connect == null) {
			return new ConnectSQL();
		}
		
		return connect;
	}
	
	private ConnectSQL () {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			conn = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
			stmt = conn.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultSet execQuery(String query) {
		try {
			rs = stmt.executeQuery(query);
			rsm = rs.getMetaData();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	
	public void execUpdate(String query) {
		try {
			stmt.executeUpdate(query);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
