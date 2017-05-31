package com.tadecather.tools;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.tadecather.unity.User;

public class DBOperate {
	
	public static String registerUser(String name, String passwd){
		Connection conn = DBHelper.getConnection();
		String sql = "INSERT INTO userAccount(userName, userPasswd) VALUES(?,?)";
		String userAccount = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setString(1, name);
			pst.setString(2, passwd);
			pst.executeUpdate();
			ResultSet rst = pst.getGeneratedKeys();
			rst.next();
			userAccount = String.valueOf(rst.getInt(1));
		
		} catch (SQLException e) {
			System.out.println("注册失败！");
			e.printStackTrace();
		}
		
		return userAccount;
		
	}
	
	
	
	
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
		
		if(rst.next() != false){
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
			if(rst.next() != true){
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
				e.printStackTrace();
			}
			
		}
	}
	
	//将文件加入到数据库
	public static int insertFileToSQL(String ownerAccount, String fileName,InputStream in) throws SQLException {
		Connection conn = DBHelper.getConnection();
		String sql = "INSERT INTO filestable(ownerAccount, fileName, filedata) VALUES(?,?,?)";
		PreparedStatement pst = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
		pst.setString(1, ownerAccount);
		pst.setString(2, fileName);
		pst.setBinaryStream(3, in);
		pst.executeUpdate();
		ResultSet rst = pst.getGeneratedKeys();
		rst.next();
		int fileID = rst.getInt(1);
		return fileID;
	}
	
	//将消息加入到数据库
	
		public static void insertMessageToSQL(String senderAcccount,String receiverAccount,
				String message, int fileID) {
			Connection conn = DBHelper.getConnection();
			String sql = "INSERT INTO messagesof" + senderAcccount +
					"(receiverAccount, message, fileID) VALUES(?,?,?)";
			
			try {
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setString(1, receiverAccount);
				pst.setString(2, message);
				pst.setInt(3, fileID);
				pst.execute();
			} catch (SQLException e) {
				System.out.println("消息插入数据库失败！");
				e.printStackTrace();
			}
			
		}
	
	   //删除指定账户
		public static boolean deleteUser(String account){
			Connection conn = DBHelper.getConnection();
			String sql = "DELETE  FROM userAccount WHERE userID = ?";
			try {
				PreparedStatement pst = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
				pst.setString(1, account);
				pst.executeUpdate();
			} catch (SQLException e) {
				System.out.println("注册失败！");
				e.printStackTrace();
				return false;
			}
			return true;
			
		}
		
	//更改指定用户的密码
	public static boolean updateUserpasswd(String account, String passwd){
		Connection conn = DBHelper.getConnection();
		String sql = "UPDATE userAccount SET userPasswd=? where userID = ?";
		try {
			PreparedStatement pst = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setString(1, passwd);
			pst.setString(2, account);
			pst.executeUpdate();
		} catch (SQLException e) {
			System.out.println("修改失败！");
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
}








