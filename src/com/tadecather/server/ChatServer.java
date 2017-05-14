package com.tadecather.server;



import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * �����
 * @author TAD
 *
 */
public class ChatServer {
	
	//һ������socket��������Ӧ�˺ŵ�Map
	public static Map<String, ServerThread> asMap = new HashMap<String, ServerThread>();
	
	public ChatServer(){
		Socket socket = null;
		int count = 0;
		ServerThread st = null;
		try {
			//����һ�������Socket��ServeSocket��ָ���󶨵Ķ˿ڣ��������˶˿�
			@SuppressWarnings("resource")
			ServerSocket serverSocket  = new ServerSocket(8888);
			//����ServerSocket accept() ���������˿ںţ��ȴ��ͻ��˵�����
			System.out.println("*********�����������������ȴ��ͻ�������******");
			while(true){
				socket = serverSocket.accept();
				
				System.out.println("success fully!");
				
				st = new ServerThread(socket);
				st.start();

				InetAddress address =socket.getInetAddress();
				System.out.println("��ַ�ǣ�" + address.getHostAddress());
				System.out.println("�ͻ���" + (++count) + "�ţ�");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
