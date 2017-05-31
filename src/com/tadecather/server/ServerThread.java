package com.tadecather.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Map.Entry;
import com.tadecather.tools.DBOperate;
import com.tadecather.unity.User;

public class ServerThread extends Thread{
	
	Socket socket = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	DBOperate dbo = null;
	
	String myAccount = null;
	
	Boolean isStart = true;
	
	//消息缓冲器
	byte[] bufferMes = new byte[1024];
	
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		try {
			
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			while(isStart){
				byte [] mesBuffer = new byte[1024];
				
				dis.read(mesBuffer,0,2);
				//获取2位消息类型
				int type = Integer.parseInt(new String(mesBuffer).trim());
				
				System.out.println();
				
				//读取12位的账号
				dis.read(mesBuffer, 0, 12);
				
				myAccount = (new String(mesBuffer)).trim();
				System.out.println("MessageType = " + type + "account = " + myAccount + "---");
				
				
				//根据消息类型进行不不同的操作
				switch(type){
					case 10 : checkAccount(myAccount);break;
					case 11 : checkFrinedAccount(myAccount);break;
					case 12 : sendOridinMessageToFriend(myAccount);break;
					case 13 : 
					case 14 : receiveFileAndSend(myAccount);break;
					case 15 : registerAccount();
					case 19 : saveAccountAndSocket();break;
					default : sendErrorMessage();
				}

			}
			
			
		} catch (SocketException e) {
			ChatServer.asMap.remove(myAccount);
			System.out.println("客户端下线！");
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(dis != null)
					dis.close();
				if(dos != null)
					dos.close();
				if(socket != null)
					socket.close();
				isStart = false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	private void registerAccount() throws IOException {
		
		bufferMes = new byte[1024];
		//读取20位的密码
		dis.read(bufferMes, 0, 20);
		String passwd  = new String(bufferMes).trim();
		//清空数组缓存
		bufferMes = null;
		bufferMes = new byte[1024];
	
		dis.read(bufferMes, 0, bufferMes.length);
		String name = new String(bufferMes).trim();
		
		String account = DBOperate.registerUser(name, passwd);
		
		//发送成功注册消息，并将注册的账号发送给客户端
		
		sendRegisterSuccessfulMEssage(account);
		
		
	}

	

	//接受和转发文件
	private void receiveFileAndSend(String ownerAccount) throws IOException {
		ServerThread friendThread = null;
		//接受朋友账号和文件名称
		dis.read(bufferMes, 0, 12);
  		String friendAccount = new String(bufferMes).trim();
  		dis.read(bufferMes, 0, 12);
  		int fileLength = Integer.parseInt(new String(bufferMes).trim());
  		System.out.println(fileLength);
  		dis.read(bufferMes, 0, bufferMes.length);
  		String fileName = new String(bufferMes).trim();
  		FileOutputStream fos = new FileOutputStream(new File(".//SeverSouecr//picture//" + fileName));
  		
  		//获取朋友账户的socket给他发送消息
  		for(Entry<String,ServerThread> entry : ChatServer.asMap.entrySet()){
  			if(entry.getKey().equals(friendAccount)){
  				friendThread = entry.getValue();
  			}
  		}
  		
  		System.out.println(friendAccount);
  		
  		//朋友不在线就发送不在线消息
  		if(friendThread != null){
  			//先将文件的具体信息发送过去
  			friendThread.sendMessage((String.valueOf(82) + String.format("%-12s", friendAccount)
			 + String.format("%-12s",String.valueOf(fileLength)) + fileName).getBytes());
  		}else{
  			sendFriendNotOnlineMessage();
  		}
  		
  		int length = 0;
  		byte[] inputByte = new byte[1024 * 100];
  		System.out.println("开始接收数据.......");
  		
  		while((length = dis.read(inputByte)) != -1){
  			
  			if(friendThread != null){
  			//发送给指定好友
  	  			friendThread.dos.write(inputByte, 0 ,length);
  			}
  			
  			//写入到指定文件夹中
			fos.write(inputByte, 0 ,length);
			fos.flush();
			fileLength -= length;
			if(fileLength <= 0){
				break;
			}
  		}
  
  		fos.close();
  		System.out.println("完成接收！");
  	
  		File file = new File(".//SeverSouecr//picture//" + fileName);
  		int fileID = upLoadFileToSQL(ownerAccount,file.getName(),new FileInputStream(file));
  		
  		System.out.println("文件插入数据库成功");
  		
  		String message = "";
  		//将消息记录插入数据库中
  		upLodeMessageToSQL(ownerAccount, friendAccount, message, fileID);
  		//file.delete();
  	
	}
	
	
	//将文件上传到数据库
	private int upLoadFileToSQL(String ownerAccount, String fileName, FileInputStream fin) {
		int fileID = 0;
		try {
			 fileID = DBOperate.insertFileToSQL(ownerAccount, fileName, fin);
		} catch (SQLException e) {
			System.out.println("文件插入数据库失败");
			e.printStackTrace();
		}
		return fileID;
	}
	
	//将消息上传到数据库中
	private void upLodeMessageToSQL(String ownerAccount,String receiverAccount,String message, int fileID) {
		
		//先插入到自己的消息记录中去
		DBOperate.insertMessageToSQL(ownerAccount, receiverAccount, message, fileID);
	    //再插入到朋友的消息记录中
		DBOperate.insertMessageToSQL(receiverAccount, ownerAccount, message, fileID);
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
				//1,表示没有文件.是我预先上传的一张图片
				upLodeMessageToSQL(ownerAccount, friendAccount, message, 1);
				
				return;
			}
		}
		sendFriendNotOnlineMessage();
			
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
	
			dis.close();
			dos.close();
			socket.close();
			isStart = false;
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
			System.out.println("senndmess");
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
	
	
	//发送消息时，对方不在线时发送给客户端的回执
	private void sendFriendNotOnlineMessage() {
		
		sendMessage((String.valueOf(81) + "FriendNotOnline Message!").getBytes());
		
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
	

	
	//发送注册成功信息 97
		private void sendRegisterSuccessfulMEssage(String account) {
			byte[] message  = (String.valueOf(96) + String.format("%-12s", account)).getBytes();
			sendMessage(message);
			
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










