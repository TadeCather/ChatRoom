 package com.tadecather.tools;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBHelper {

	public static final String url = "jdbc:mysql://127.0.0.1/chatroom";
	public static final String driverName = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String passwd = "root";
	
	private static Connection conn;
	
	
	//æ≤Ã¨”Ôæ‰øÈ£¨”≈œ»º”‘ÿ
	static {
		try{
			Class.forName(driverName);
			conn = DriverManager.getConnection(url, user, passwd);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static Connection getConnection(){
		return conn;
	}
	

}
