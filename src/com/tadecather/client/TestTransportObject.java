package com.tadecather.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import com.tadecather.unity.User;

public class TestTransportObject {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket socket  = new Socket("localhost", 8888);
		
		ObjectInputStream ois = null; 
		
		//����һ��˵���ǣ�ÿ��ObjectStream�����ɹ�����ȶ�ȡ�ĸ��ֽڣ����Գ�ʼ���Ĺ��̾�˯��ס
		//����ÿ��ObjectInputStream���ܵ�һ���������Ҫ���½��г�ʼ��
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		
		
		User user = new User(666,"030290","���");
		
		oos.writeObject(user);
		
	}

}
