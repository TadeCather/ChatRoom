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
	
	//��Ϣ������
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
				//��ȡ2λ��Ϣ����
				int type = Integer.parseInt(new String(mesBuffer).trim());
				
				System.out.println();
				
				//��ȡ12λ���˺�
				dis.read(mesBuffer, 0, 12);
				
				myAccount = (new String(mesBuffer)).trim();
				System.out.println("MessageType = " + type + "account = " + myAccount + "---");
				
				
				//������Ϣ���ͽ��в���ͬ�Ĳ���
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
			System.out.println("�ͻ������ߣ�");
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
		//��ȡ20λ������
		dis.read(bufferMes, 0, 20);
		String passwd  = new String(bufferMes).trim();
		//������黺��
		bufferMes = null;
		bufferMes = new byte[1024];
	
		dis.read(bufferMes, 0, bufferMes.length);
		String name = new String(bufferMes).trim();
		
		String account = DBOperate.registerUser(name, passwd);
		
		//���ͳɹ�ע����Ϣ������ע����˺ŷ��͸��ͻ���
		
		sendRegisterSuccessfulMEssage(account);
		
		
	}

	

	//���ܺ�ת���ļ�
	private void receiveFileAndSend(String ownerAccount) throws IOException {
		ServerThread friendThread = null;
		//���������˺ź��ļ�����
		dis.read(bufferMes, 0, 12);
  		String friendAccount = new String(bufferMes).trim();
  		dis.read(bufferMes, 0, 12);
  		int fileLength = Integer.parseInt(new String(bufferMes).trim());
  		System.out.println(fileLength);
  		dis.read(bufferMes, 0, bufferMes.length);
  		String fileName = new String(bufferMes).trim();
  		FileOutputStream fos = new FileOutputStream(new File(".//SeverSouecr//picture//" + fileName));
  		
  		//��ȡ�����˻���socket����������Ϣ
  		for(Entry<String,ServerThread> entry : ChatServer.asMap.entrySet()){
  			if(entry.getKey().equals(friendAccount)){
  				friendThread = entry.getValue();
  			}
  		}
  		
  		System.out.println(friendAccount);
  		
  		//���Ѳ����߾ͷ��Ͳ�������Ϣ
  		if(friendThread != null){
  			//�Ƚ��ļ��ľ�����Ϣ���͹�ȥ
  			friendThread.sendMessage((String.valueOf(82) + String.format("%-12s", friendAccount)
			 + String.format("%-12s",String.valueOf(fileLength)) + fileName).getBytes());
  		}else{
  			sendFriendNotOnlineMessage();
  		}
  		
  		int length = 0;
  		byte[] inputByte = new byte[1024 * 100];
  		System.out.println("��ʼ��������.......");
  		
  		while((length = dis.read(inputByte)) != -1){
  			
  			if(friendThread != null){
  			//���͸�ָ������
  	  			friendThread.dos.write(inputByte, 0 ,length);
  			}
  			
  			//д�뵽ָ���ļ�����
			fos.write(inputByte, 0 ,length);
			fos.flush();
			fileLength -= length;
			if(fileLength <= 0){
				break;
			}
  		}
  
  		fos.close();
  		System.out.println("��ɽ��գ�");
  	
  		File file = new File(".//SeverSouecr//picture//" + fileName);
  		int fileID = upLoadFileToSQL(ownerAccount,file.getName(),new FileInputStream(file));
  		
  		System.out.println("�ļ��������ݿ�ɹ�");
  		
  		String message = "";
  		//����Ϣ��¼�������ݿ���
  		upLodeMessageToSQL(ownerAccount, friendAccount, message, fileID);
  		//file.delete();
  	
	}
	
	
	//���ļ��ϴ������ݿ�
	private int upLoadFileToSQL(String ownerAccount, String fileName, FileInputStream fin) {
		int fileID = 0;
		try {
			 fileID = DBOperate.insertFileToSQL(ownerAccount, fileName, fin);
		} catch (SQLException e) {
			System.out.println("�ļ��������ݿ�ʧ��");
			e.printStackTrace();
		}
		return fileID;
	}
	
	//����Ϣ�ϴ������ݿ���
	private void upLodeMessageToSQL(String ownerAccount,String receiverAccount,String message, int fileID) {
		
		//�Ȳ��뵽�Լ�����Ϣ��¼��ȥ
		DBOperate.insertMessageToSQL(ownerAccount, receiverAccount, message, fileID);
	    //�ٲ��뵽���ѵ���Ϣ��¼��
		DBOperate.insertMessageToSQL(receiverAccount, ownerAccount, message, fileID);
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
				//1,��ʾû���ļ�.����Ԥ���ϴ���һ��ͼƬ
				upLodeMessageToSQL(ownerAccount, friendAccount, message, 1);
				
				return;
			}
		}
		sendFriendNotOnlineMessage();
			
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
	
			dis.close();
			dos.close();
			socket.close();
			isStart = false;
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
			System.out.println("senndmess");
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
	
	
	//������Ϣʱ���Է�������ʱ���͸��ͻ��˵Ļ�ִ
	private void sendFriendNotOnlineMessage() {
		
		sendMessage((String.valueOf(81) + "FriendNotOnline Message!").getBytes());
		
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
	

	
	//����ע��ɹ���Ϣ 97
		private void sendRegisterSuccessfulMEssage(String account) {
			byte[] message  = (String.valueOf(96) + String.format("%-12s", account)).getBytes();
			sendMessage(message);
			
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










