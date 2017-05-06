package com.tadecather.tools;

import java.sql.SQLException;
import java.util.List;

import com.tadecather.unity.User;

public class UserLoginHelper {
	
	

	public static void main(String[] args) throws SQLException {
		DBOperate dbo = new DBOperate();
		List<User> users = dbo.getAllUser();
		for(User user : users){
			System.out.println(user.getUser_ID() + "\t" + user.getUser_name() + "\t"
					+ user.getUser_passwd());
		}
		
		User user = dbo.getUserByID(666);
		System.out.println(user.getUser_ID() + "\t" + user.getUser_name() + "\t"
				+ user.getUser_passwd());
	}
	
}
