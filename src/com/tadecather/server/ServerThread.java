package com.tadecather.server;


import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map.Entry;

import com.tadecather.tools.DBOperate;
import com.tadecather.unity.User;

public class ServerThread extends Thread{
	
	Socket socket = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	DBOperate dbo = null;
	
	String myAccount = null;
	
	//消息缓冲器
	byte[] bufferMes = new byte[1024];
	
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		try {
			
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			
			String info = null;
		
			while(true){
				
				
				byte [] mesBuffer = new byte[1024];
				
				dis.read(mesBuffer,0,2);
				//获取2位消息类型
				int type = Integer.parseInt(new String(mesBuffer).trim());
				
				System.out.println("MessageType:" + type);
				
				//读取12位的账号
				dis.read(mesBuffer, 0, 12);
				
				myAccount = (new String(mesBuffer)).trim();
				System.out.println("account" + myAccount + "---");
				
				
				
				switch(type){
					case 10 : checkAccount(myAccount);break;
					case 11 : checkFrinedAccount(myAccount);break;
					case 12 : sendOridinMessageToFriend(myAccount);break;
					case 19 : saveAccountAndSocket();break;
					default : sendErrorMessage();
				}
				
				
				
			}
			
			//判断用户输入的信息是否正确！
			
		} catch (Exception e) {
			
			
			ChatServer.asMap.remove(myAccount);
			
			System.out.println("客户端下线！");
			e.printStackTrace();
		} finally{
			try {
				if(dis != null)
					dis.close();
				if(dos != null)
					dos.close();
				if(socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//发送消息给指定的客户端
	private void sendOridinMessageToFriend(String ownerAccount) throws IOException {
  		dis.read(bufferMes, 0, 12);
  		
  		String friendAccount = new String(bufferMes).trim();
  		
  		System.out.println(friendAccount);
  		
  		dis.read(bufferMes, 0, bufferMes.length);
  		
  		String message = new String(bufferMes).trim(); 
  		
  		System.out.println(message);
  		String str1 = String.valueOf(80) + String.format("%-12s", ownerAccount) + message;
  		System.out.println(str1);
  		
  		byte [] messageByte = (String.valueOf(80) + String.format("%-12s", ownerAccount) + message).getBytes();
  		
		for(Entry<String, ServerThread>  entry : ChatServer.asMap.entrySet()){
			if(entry.getKey().equals(friendAccount)){
				entry.getValue().sendMessage(messageByte);
				break;
			}
		}
			
	}

	//保存socket和自己账户的信息组
	private void saveAccountAndSocket() throws IOException {

		String str1 = readSomething();
		System.out.println(str1);
		
		if(! ChatServer.asMap.containsKey(myAccount)){
			ChatServer.asMap.put(myAccount, this);
			sendReceivedMessage();
		}else{
			sendRepeatLoginMessage();
		}
		
	
	}

	

	

	//检查用户用户账号密码是否匹配
	private void checkAccount( String account) {
		byte[] mesBuffer = new byte[1024];
		
		try {
			dis.read(mesBuffer, 0, 20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String passwd = (new String(mesBuffer)).trim();
		
		System.out.println(passwd+"-");
		User user  = new User();
		user.setUserAccount(account);
		user.setUserPasswd(passwd);
		
		boolean b = checkoutPasswd(user);
		if(b){
			sendSuccessLoginMessage();
		}else{
			sendErrorLoginMessage();
		}
		
	}
	
	//通过UserID获取数据库的用户信息，并检查密码会否正确
	public boolean checkoutPasswd(User userlogin){
		
		User usersql = null;
		
		dbo = new DBOperate();
		
		String IDlogin = userlogin.getUserAccount();
		try {
			usersql = dbo.getUserByID(IDlogin);
		} catch (SQLException e) {
			System.out.println("账户不存在！请注册！");
			e.printStackTrace();
			return false;
		}
		
		
		if(userlogin.getUserPasswd().equals(usersql.getUserPasswd())){
			System.out.println("密码相同！");
			return true;
		}else{
			return false;
		}		
	}
	
	//检查好友列表中是否存在所要求聊天的账户
	private void checkFrinedAccount(String myAccount) throws IOException {
		
		
		byte [] account = new byte[12];
		dis.read(account,0,12);
		String friendAccount = new String(account).trim();
		
		//连接数据库查找好友是自己的
		dbo = new DBOperate();
		
		boolean isFreind = dbo.checkIsFrind(myAccount,friendAccount);
		if(isFreind){
			//发送好友存在信息
			sendFriendExistMessage();
		}else{
			//发送好友不存在信息
			sendFriendNotExistMessage();
		}
		
		
	}
	
	//发送成功收到账号回执
	private void sendReceivedMessage() {
		//成功收到消息登陆消息
		sendMessage((String.valueOf(94) + "SuccessfulReceivedAccount Message!").getBytes());
		
	}
	
	//发送重复登录回执
	private void sendRepeatLoginMessage() {
		sendMessage((String.valueOf(95) + "RepeatLogin Message!").getBytes());
		
	}
	

	//发送登陆失败信息
	private void sendSuccessLoginMessage() {
		//登陆成功类型 90
		sendMessage((String.valueOf(90) + "SuccessfulLogin Message!").getBytes());
		
	}

	//发送登陆失败信息
	private void sendErrorLoginMessage() {
		//登陆失败类型 91
		sendMessage((String.valueOf(91) + "ErrorLogin Message!").getBytes());
	}
	
	//发送好友存在消息
	
	private void sendFriendExistMessage() {
		sendMessage((String.valueOf(92) + "FriendExist Message!").getBytes());
	}
	//发送好友不存在消息
	private void sendFriendNotExistMessage() {
		sendMessage((String.valueOf(93) + "FriendNotExist Message!").getBytes());
	}
	//发送消息错误信息
	private void sendErrorMessage() {
		//非法信息
		byte [] errorMes = null;
		
		String errInfo = "IlleageType Message!";
		//非法信息类型 99
		errorMes = (String.valueOf(99) + errInfo).getBytes();
		
		sendMessage(errorMes);
		
		
	}
	//发送消息
	private void sendMessage(byte[] mes) {
		try {
			dos.write(mes, 0, mes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void punblish(String mes) throws IOException{
		
//		for(ServerThread st: ChatServer.stList){
//			//Bug修复：只会给自己的客户端发送消息
//			//原因： 将st.dos.write(mes)写成dos.write(mes);
//			if(!st.equals(this)){
//				st.dos.writeUTF( "-----from server------\n" + mes);
//			}
//		}
	}
	//读取剩余的信息
			private String readSomething() throws IOException {
				byte[] bufferMessage = new byte[1024];
				dis.read(bufferMessage,0,bufferMessage.length);
				return new String(bufferMessage).trim();
				
			}
	
	
}










