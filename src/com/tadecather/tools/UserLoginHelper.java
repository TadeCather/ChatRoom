package com.tadecather.tools;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class UserLoginHelper {

	public static void main(String[] args) throws SQLException, FileNotFoundException {
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
		
//		Boolean b = new DBOperate().checkIsFrind("10000", "1");
//		System.out.println(b);
//		}
		
//		int fileID = dbo.insertFileToSQL("1","123.PNG",new FileInputStream("D:\\123.PNG"));
//		System.out.println("文件写入成功，ID是 ：" + fileID);
		
		
		DBOperate.insertMessageToSQL("1", "10000", "hello", 1);
		
		
			
		
	}	
	
}
