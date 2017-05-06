package com.tadecather.server;


import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

import com.tadecather.tools.DBOperate;
import com.tadecather.unity.User;

public class ServerThread extends Thread{
	
	Socket socket = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
//	ObjectInputStream ois = null;
//	ObjectOutputStream oos = null;
	
	
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		try {
			
//			ois = new ObjectInputStream(socket.getInputStream());
//			oos = new ObjectOutputStream(socket.getOutputStream());
			
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			String info = null;
		
			while(true){
				
				info = dis.readUTF();
				
//				User user = null;
//				
//				try {
//					user = (User)ois.readObject();
//					System.out.println("User_name" + user.getUser_name() + "UserID" + user.getUser_ID() 
//				
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				if(info.equals("14130130229030290")){//user.getUser_ID() == 666){
					
					//�������ݿ�,��������ƥ�乤��
					
//					dos.writeUTF("��½�ɹ�����ӭ�㣡");
					boolean b = checkoutPasswd(new User(666,"030290","���"));//user);
					if(b){
						System.out.println("++++++++++++++++++++++++");
						dos.writeUTF("��½�ɹ�����ӭ�㣡");
					}else{
						break;
					}
					
					
				}else if(info.equals("110120130")){
					dos.writeUTF("�˻����ڣ��������죡");
				}else{
					//Ⱥ����Ϣ
//					System.out.println("���Ƿ��������ͻ���˵��" + info);
					punblish(info);
				}
				
				System.out.println("���Ƿ��������ͻ���˵��" + info);
				
			}
			
			//�ж��û��������Ϣ�Ƿ���ȷ��
			
		} catch (IOException e) {
			ChatServer.stList.remove(this);
			
			System.out.println("�ͻ������ߣ�");
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
//			e.printStackTrace();
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
	
	
	
	public void punblish(String mes) throws IOException{
		
		for(ServerThread st: ChatServer.stList){
			//Bug�޸���ֻ����Լ��Ŀͻ��˷�����Ϣ
			//ԭ�� ��st.dos.write(mes)д��dos.write(mes);
			if(!st.equals(this)){
				st.dos.writeUTF( "-----from server------\n" + mes);
			}
		}
	}
	
	
	//ͨ��UserID��ȡ���ݿ���û���Ϣ���������������ȷ
	public boolean checkoutPasswd(User userlogin){
		
		User usersql = null;
		
		DBOperate dbo = new DBOperate();
		
		int IDlogin = userlogin.getUser_ID();
		try {
			usersql = dbo.getUserByID(IDlogin);
		} catch (SQLException e) {
			System.out.println("�˻������ڣ���ע�ᣡ");
			e.printStackTrace();
			return false;
		}
		
		
		if(userlogin.getUser_passwd().equals(usersql.getUser_passwd())){
			System.out.println("������ͬ��");
			return true;
		}else{
			return false;
		}		
	}
	
}










