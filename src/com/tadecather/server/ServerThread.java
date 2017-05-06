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
					
					//调用数据库,进行密码匹配工作
					
//					dos.writeUTF("登陆成功，欢迎你！");
					boolean b = checkoutPasswd(new User(666,"030290","大白"));//user);
					if(b){
						System.out.println("++++++++++++++++++++++++");
						dos.writeUTF("登陆成功，欢迎你！");
					}else{
						break;
					}
					
					
				}else if(info.equals("110120130")){
					dos.writeUTF("账户存在！进入聊天！");
				}else{
					//群发消息
//					System.out.println("我是服务器，客户端说：" + info);
					punblish(info);
				}
				
				System.out.println("我是服务器，客户端说：" + info);
				
			}
			
			//判断用户输入的信息是否正确！
			
		} catch (IOException e) {
			ChatServer.stList.remove(this);
			
			System.out.println("客户端下线！");
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
			//Bug修复：只会给自己的客户端发送消息
			//原因： 将st.dos.write(mes)写成dos.write(mes);
			if(!st.equals(this)){
				st.dos.writeUTF( "-----from server------\n" + mes);
			}
		}
	}
	
	
	//通过UserID获取数据库的用户信息，并检查密码会否正确
	public boolean checkoutPasswd(User userlogin){
		
		User usersql = null;
		
		DBOperate dbo = new DBOperate();
		
		int IDlogin = userlogin.getUser_ID();
		try {
			usersql = dbo.getUserByID(IDlogin);
		} catch (SQLException e) {
			System.out.println("账户不存在！请注册！");
			e.printStackTrace();
			return false;
		}
		
		
		if(userlogin.getUser_passwd().equals(usersql.getUser_passwd())){
			System.out.println("密码相同！");
			return true;
		}else{
			return false;
		}		
	}
	
}










