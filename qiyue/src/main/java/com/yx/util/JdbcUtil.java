package com.yx.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtil {

	private final static ThreadLocal<Connection> tol=new ThreadLocal<Connection>();
	public static Connection getConnection(){
		Connection conn=tol.get();
		if(conn==null){ 
			try{ 
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
				conn=DriverManager.getConnection("jdbc:derby:E:\\Program Files\\db-derby-10.14.2.0-bin\\bin\\qiyue;create=true;user=root;password=root");
				tol.set(conn); 
			}catch(Exception e){
					e.printStackTrace();
					throw new RuntimeException(e.getMessage()); 
			} 
		}
		return conn;
	}
	public static void close(ResultSet rs,Statement stm){ 
		if(rs!=null) try {rs.close();} catch (Exception e) {}
		if(stm!=null) try {stm.close();} catch (Exception e) {} 
	}
	public static void close(Connection conn){ 
		if(conn!=null) 
			try {
				conn.close();tol.remove();
				} catch (Exception e) {} 
		try {
			DriverManager.getConnection("jdbc:derby:qiyue;shutdown=true");
		} catch (SQLException e1) {
			System.out.println("¹Ø±ÕÊý¾Ý¿â");
		}
	}


}





