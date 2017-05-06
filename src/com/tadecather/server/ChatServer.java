package com.tadecather.server;



import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * 服务端
 * @author TAD
 *
 */
public class ChatServer {
	
	
	public static List<ServerThread> stList = new ArrayList<ServerThread>();
	
	
	
	

	public ChatServer(){
		Socket socket = null;
		int count = 0;
		ServerThread st = null;
		try {
			//创建一个服务端Socket，ServeSocket，指定绑定的端口，并监听此端口
			@SuppressWarnings("resource")
			ServerSocket serverSocket  = new ServerSocket(8888);
			//调用ServerSocket accept() 方法监听端口号，等待客户端的连接
			System.out.println("*********服务器即将启动，等待客户端连接******");
			while(true){
				socket = serverSocket.accept();
				
				System.out.println("success fully!");
				
				st = new ServerThread(socket);
				st.start();
				stList.add(st);
				
				InetAddress address =socket.getInetAddress();
				System.out.println("地址是：" + address.getHostAddress());
				System.out.println("客户端" + (++count) + "号！");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
