package com.tadecather.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import com.tadecather.unity.User;

public class TestTransportObject {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		Socket socket  = new Socket("localhost", 8888);
		
		ObjectInputStream ois = null; 
		
		//看到一个说法是，每次ObjectStream创建成功后会先读取四个字节，所以初始化的过程就睡卡住
		//并且每次ObjectInputStream接受到一个对象后都需要重新进行初始化
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		
		
		User user = new User(666,"030290","大白");
		
		oos.writeObject(user);
		
	}

}
