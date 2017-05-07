package com.tadecather.tools;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.tadecather.unity.User;

public class DBOperate {
	
	//查看所有用户的信息
	public List<User> getAllUser() throws SQLException{
		Connection conn = DBHelper.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rst = stmt.executeQuery("SELECT userID,userName,userPasswd FROM userAccount");
		
		List<User> users = new ArrayList<User>();
		
		while(rst.next()){
			User user = new User();
			user.setUserAccount(String.valueOf(rst.getInt("userID")));
			user.setUserName(rst.getString("userName"));
			user.setUserPasswd(rst.getString("userPasswd"));
			user.setSignUpTime(rst.getDate("signUpTime"));
			users.add(user);
		}
		
		stmt.close();
		rst.close();
		return users;
		
	}
	
	//获得指定账号的用户信息
	public User getUserByID(String iDlogin) throws SQLException{
		
		User user = new User();
		String sql = "SELECT * FROM userAccount WHERE userID=" + iDlogin;
		Connection conn = DBHelper.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rst = stmt.executeQuery(sql);
		
		while(rst.next()){
			user.setUserAccount(String.valueOf(rst.getInt("userID")));
			user.setUserName(rst.getString("userName"));
			user.setUserPasswd(rst.getString("userPasswd"));
			user.setSignUpTime(rst.getDate("signUpTime"));
		}
		
		stmt.close();
		rst.close();
		
		return user;
		
		
	}
	
	//查看指定账户的好友是否存在
	public boolean checkIsFrind(String myAccount, String friendAccount)  {
		
		PreparedStatement pst = null;
		ResultSet rst = null;
		String sql = null;
		System.out.println(friendAccount);
		try {
			Connection conn = DBHelper.getConnection();
			sql = "SELECT * FROM friendsof" + myAccount + " WHERE friendAccount=?";
			pst = conn.prepareStatement(sql);
			pst.setString(1, friendAccount);
			rst = pst.executeQuery();
			rst.next();
			Integer account = rst.getInt("friendAccount");
			if(account.equals(null)){
				return false;
			}else return true;
		} catch (SQLException e) {
			System.out.println("查询好友不存在！");
			e.printStackTrace();
			return false;
		}finally{
			try {
				rst.close();
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
			
		
	}
	
	
	
	
	
	
	
}








