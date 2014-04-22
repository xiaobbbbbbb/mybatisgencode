package com.ecarinfo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	
	static {
		try {
			//加载驱动
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static String URL;
	public static String USERNAME;
	public static String PASSWORD;
	
	public static final Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static final void close(Connection conn,Statement stmt,ResultSet rs) {
		if(rs != null) {
			try {rs.close();} catch (SQLException e) {}
		}
		if(stmt != null) {
			try {stmt.close();} catch (SQLException e) {}
		}
		if(conn != null) {
			try {conn.close();} catch (SQLException e) {}
		}
	}
}
