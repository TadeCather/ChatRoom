package com.tadecather.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tadecather.unity.User;

public class DBOperate {
	
	public List<User> getAllUser() throws SQLException{
		Connection conn = DBHelper.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rst = stmt.executeQuery("SELECT user_ID,user_name,user_passwd FROM userchatroom");
		
		List<User> users = new ArrayList<User>();
		
		while(rst.next()){
			User user = new User();
			user.setUser_ID(rst.getInt("user_ID"));
			user.setUser_name(rst.getString("user_name"));
			user.setUser_passwd(rst.getString("user_passwd"));
			users.add(user);
		}
		
		return users;
		
	}
	
	public User getUserByID(int userID) throws SQLException{
		
		User user = new User();
		String sql = "SELECT * FROM userchatroom WHERE user_ID=" + userID;
		System.out.println("SELECT * FROM userchatroom WHERE user_ID=1;");
		Connection conn = DBHelper.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rst = stmt.executeQuery(sql);
		
		while(rst.next()){
			
			user.setUser_ID(rst.getInt("user_ID"));
			user.setUser_name(rst.getString("user_name"));
			user.setUser_passwd(rst.getString("user_passwd"));
		}
		
		
		stmt.close();
		rst.close();

		
		return user;
		
		
	}
	
	
	
	
	
	
	
}








