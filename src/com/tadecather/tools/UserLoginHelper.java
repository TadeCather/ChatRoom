package com.tadecather.tools;

import java.sql.SQLException;
import java.util.List;

import com.tadecather.unity.User;

public class UserLoginHelper {
	
	

	public static void main(String[] args) throws SQLException {
//		DBOperate dbo = new DBOperate();
//		List<User> users = dbo.getAllUser();
//		for(User user : users){
//			System.out.println(user.getUserAccount() + "\t" + user.getUserName() + "\t"
//					+ user.getUserAccount());
//		}
//		
//		
//		User user = dbo.getUserByID("1");
//		System.out.println(user.getUserAccount() + "\t" + user.getUserName() + "\t"
//				+ user.getUserPasswd());
		
		Boolean b = new DBOperate().checkIsFrind("10000", "1");
		System.out.println(b);
		}
	
}
