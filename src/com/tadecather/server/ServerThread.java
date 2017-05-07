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
	
	//��Ϣ������
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
				//��ȡ2λ��Ϣ����
				int type = Integer.parseInt(new String(mesBuffer).trim());
				
				System.out.println("MessageType:" + type);
				
				//��ȡ12λ���˺�
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
			
			//�ж��û��������Ϣ�Ƿ���ȷ��
			
		} catch (Exception e) {
			
			
			ChatServer.asMap.remove(myAccount);
			
			System.out.println("�ͻ������ߣ�");
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
	
	//������Ϣ��ָ���Ŀͻ���
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

	//����socket���Լ��˻�����Ϣ��
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

	

	

	//����û��û��˺������Ƿ�ƥ��
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
	
	//ͨ��UserID��ȡ���ݿ���û���Ϣ���������������ȷ
	public boolean checkoutPasswd(User userlogin){
		
		User usersql = null;
		
		dbo = new DBOperate();
		
		String IDlogin = userlogin.getUserAccount();
		try {
			usersql = dbo.getUserByID(IDlogin);
		} catch (SQLException e) {
			System.out.println("�˻������ڣ���ע�ᣡ");
			e.printStackTrace();
			return false;
		}
		
		
		if(userlogin.getUserPasswd().equals(usersql.getUserPasswd())){
			System.out.println("������ͬ��");
			return true;
		}else{
			return false;
		}		
	}
	
	//�������б����Ƿ������Ҫ��������˻�
	private void checkFrinedAccount(String myAccount) throws IOException {
		
		
		byte [] account = new byte[12];
		dis.read(account,0,12);
		String friendAccount = new String(account).trim();
		
		//�������ݿ���Һ������Լ���
		dbo = new DBOperate();
		
		boolean isFreind = dbo.checkIsFrind(myAccount,friendAccount);
		if(isFreind){
			//���ͺ��Ѵ�����Ϣ
			sendFriendExistMessage();
		}else{
			//���ͺ��Ѳ�������Ϣ
			sendFriendNotExistMessage();
		}
		
		
	}
	
	//���ͳɹ��յ��˺Ż�ִ
	private void sendReceivedMessage() {
		//�ɹ��յ���Ϣ��½��Ϣ
		sendMessage((String.valueOf(94) + "SuccessfulReceivedAccount Message!").getBytes());
		
	}
	
	//�����ظ���¼��ִ
	private void sendRepeatLoginMessage() {
		sendMessage((String.valueOf(95) + "RepeatLogin Message!").getBytes());
		
	}
	

	//���͵�½ʧ����Ϣ
	private void sendSuccessLoginMessage() {
		//��½�ɹ����� 90
		sendMessage((String.valueOf(90) + "SuccessfulLogin Message!").getBytes());
		
	}

	//���͵�½ʧ����Ϣ
	private void sendErrorLoginMessage() {
		//��½ʧ������ 91
		sendMessage((String.valueOf(91) + "ErrorLogin Message!").getBytes());
	}
	
	//���ͺ��Ѵ�����Ϣ
	
	private void sendFriendExistMessage() {
		sendMessage((String.valueOf(92) + "FriendExist Message!").getBytes());
	}
	//���ͺ��Ѳ�������Ϣ
	private void sendFriendNotExistMessage() {
		sendMessage((String.valueOf(93) + "FriendNotExist Message!").getBytes());
	}
	//������Ϣ������Ϣ
	private void sendErrorMessage() {
		//�Ƿ���Ϣ
		byte [] errorMes = null;
		
		String errInfo = "IlleageType Message!";
		//�Ƿ���Ϣ���� 99
		errorMes = (String.valueOf(99) + errInfo).getBytes();
		
		sendMessage(errorMes);
		
		
	}
	//������Ϣ
	private void sendMessage(byte[] mes) {
		try {
			dos.write(mes, 0, mes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void punblish(String mes) throws IOException{
		
//		for(ServerThread st: ChatServer.stList){
//			//Bug�޸���ֻ����Լ��Ŀͻ��˷�����Ϣ
//			//ԭ�� ��st.dos.write(mes)д��dos.write(mes);
//			if(!st.equals(this)){
//				st.dos.writeUTF( "-----from server------\n" + mes);
//			}
//		}
	}
	//��ȡʣ�����Ϣ
			private String readSomething() throws IOException {
				byte[] bufferMessage = new byte[1024];
				dis.read(bufferMessage,0,bufferMessage.length);
				return new String(bufferMessage).trim();
				
			}
	
	
}










