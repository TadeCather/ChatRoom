package com.tadecather.tools;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import com.tadecather.unity.User;

public class UserLoginHelper {

	public static void main(String[] args) throws SQLException, FileNotFoundException {
		DBOperate dbo = new DBOperate();

		
		DBOperate.registerUser("Ϲ����", "lllllllll");
		
		User user = dbo.getUserByID("1");
		System.out.println(user.getUserAccount() + "\t" + user.getUserName() + "\t"
				+ user.getUserPasswd());
	
		boolean b3 = DBOperate.deleteUser("10007");
		if(b3){
			System.out.println("ɾ���ɹ���");
		}
		
		boolean b4 = DBOperate.updateUserpasswd("10008","987654");
		if(b4){
			System.out.println("���ĳɹ���");
		}
			
		
	}	
	
}
