package com.tadecather.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import com.sun.corba.se.impl.encoding.BufferManagerFactory;
import com.tadecather.unity.User;

/**
 * 客户端的登陆UI
 * @author TAD
 * 2017-5-1
 */
@SuppressWarnings("serial")
public class ClientUI extends JFrame{
	
	
	private JPanel panelName = new JPanel();
	private JPanel panelPasswd = new JPanel();
	private JPanel panelButton = new JPanel();
	private JPanel imagePanel;
	
	private JLabel JLname = new JLabel("UserName：");
	private JLabel JLpasswd = new JLabel("Passwd：    ");
	private JTextField JTnameInput  = new JTextField(15);
	private JPasswordField JTpassswdInput  = new JPasswordField(15);
	
	private JButton JBlogin = new JButton("LOGIN");
	private JButton JBcancle = new JButton("CONCLE");
 	
	String imagePath = "d://123.PNG";
	Image image = Toolkit.getDefaultToolkit().createImage(imagePath);
	
	
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	
	private String data = null;
	
	public boolean isLogin = false;
	
	//重复登录标识码
	private int isLogined = 0;
	
	public User userCurrent = null;
	
	
	public static boolean isConnect = false;
	
	public String getData() {
		return data;
	}
	
	

	public void setData(String data) {
		this.data = data;
	}

	public ClientUI(){
		
		//绘制背景图片
		imagePanel = new JPanel(){
			protected void paintChildren(Graphics g){
				g.drawImage(image, -50, -30, this);
				super.paintChildren(g);
			}
		};
		
	
		panelName.add(JLname);
		panelName.add(JTnameInput);
		JTnameInput.setText("1");
		panelName.setOpaque(false);
		
		panelPasswd.add(JLpasswd);
		panelPasswd.add(JTpassswdInput);
		JTpassswdInput.setText("iloveyou");
		panelPasswd.setOpaque(false);
		
		panelButton.add(JBlogin);
		panelButton.add(JBcancle);
		panelButton.setOpaque(false);
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
		
		imagePanel.add(panelName);
		imagePanel.add(panelPasswd);
		imagePanel.add(panelButton);
	
		imagePanel.setBorder(new MatteBorder(80, 130, 50, 100, Color.RED));
		imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
		add(imagePanel, BorderLayout.CENTER);
		
		this.setLocation(700, 330);
		this.setSize(540, 420);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("欢迎登陆！");
		this.setResizable(false);
		this.setVisible(true);
		
		
		//连接服务器，初始化socket，并获得socket的输入输出流对象
		connect();
		
		//建立新的线程，用来监控服务器的返回数据
		new Thread(new RecThread(socket)).start();
		
		
		//对两个按钮添加事件响应
		JBlogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//发送登陆信息
				sendLoginMessage();
				
				
				//将主线程阻塞在此，直到接收到服务器消息
				while(true){
					//如果data的值不是null，就执行操作，并跳出循环
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if(isLogin){
						
						action();
						//将返回的消息重置为空
						break;
					}
				}
				
				
			}
		});
		
		JBcancle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		} );
		
	}
	
	//连接服务器，初始化socket，并获得socket的输入输出流对象
	public void connect(){
		try {
			socket = new Socket("localhost", 8888);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			isConnect = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("连接服务器失败！");
			JOptionPane.showMessageDialog(null, "警告信息" , "未找到服务器，请检查网络设置！",
					JOptionPane.ERROR_MESSAGE);
			
			System.out.println("请检查你的网络设置！");
		}
	}
	
	
	//断开连接
	public void disConnect(){
		
		try {
			dis.close();
			dos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isConnect = false;
	}
	//发送登陆信息
	public void sendLoginMessage(){
		String name = JTnameInput.getText();
		String passwd = JTpassswdInput.getText();
		System.out.println("Name=" + name + " passwd: " + passwd);
		String logMes = String.format("%-12s", name) + String.format("%-20s",passwd);
		System.out.println(logMes+"-");
		//请求登陆类型10
		sendMessage((String.valueOf(10) + logMes).getBytes());
		
	}
	
	//发送信息
	public void sendMessage(byte[] message){
		System.out.println(message.length);
		if(isConnect){
			try {
				dos.write(message, 0, message.length);
				//使当前的线程阻塞200毫秒
				Thread.sleep(200);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}else{
				System.out.println("FUCK YOU");
		}
		
	}
	
	//然后执行登陆成功后相应的步骤
	public void action(){
		
		//便于服务端保存自己socket的自己账号
		sendMessage((String.valueOf(19) + String.format("%-12s", JTnameInput.getText())+ "tadecather").getBytes());
		
		while(true){
			if(isLogined == 1){
				userCurrent = new User();
				userCurrent.setUserAccount(JTnameInput.getText());
				this.setVisible(false);
				new ChatChooseUI(this);
				break;
			}
			if(isLogined == 2){
				disConnect();
				break;
			}
		}
		
		
	}
	
	
	
	
	//建立一个线程，用来监控从服务端发过来的数据
	private class RecThread implements Runnable{

		Socket socket = null;
		public RecThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			byte[] bufferMes = new byte[1024];
			while (isConnect){
				if(dis != null){
					try { 
						dis.read(bufferMes, 0, 2);
					
						int mesType = Integer.parseInt(new String(bufferMes).trim());
						
						System.out.println("MessageType:" + mesType);
						
						
						
						switch(mesType){
							
							case 90 : {String str1 = readSomething();isLogin = true; System.out.println(str1); break;}
							case 91 : {String str1 = readSomething();System.out.println("登陆失败！");JTpassswdInput.setText("");System.out.println(str1); break;}
							case 92 : {String str1 = readSomething();InputAccountUI.isFriendexist = 1;System.out.println(str1);break;}
							case 93 : {String str1 = readSomething();InputAccountUI.isFriendexist = 2;System.out.println("好友不存在，请重新选择！");System.out.println(str1);break;}
							case 94 : {String str1 = readSomething();isLogined = 1;System.out.println("服务器准备工作做好了");System.out.println(str1);break;}
							case 95 : {String str1 = readSomething();isLogined = 2;System.out.println(str1);System.out.println("请不要重复登录同一个账号！");break;}
							
							case 80 : {receiveOthersMes();break;}
							default : {System.out.println("Error Information from Server");}
							}
						
						
						
						
						
				} catch (IOException e) {
					
					isConnect = false;
					}
			
				
				}
			}
		
		}
        private void receiveOthersMes() throws IOException {
        	byte[] bufferMes = new byte[1024];
			dis.read(bufferMes, 0, 12);
			ChatUI.messageowenAccount = new String(bufferMes).trim();
			System.out.println(ChatUI.messageowenAccount);
			int length = dis.read(bufferMes, 0, bufferMes.length);
			System.out.println("MesLength: " + length);
			ChatUI.messagefromOther = new String(bufferMes).trim();	
		}

		//读取剩余的信息
		private String readSomething() throws IOException {
			byte[] bufferMessage = new byte[1024];
			dis.read(bufferMessage,0,bufferMessage.length);
			return new String(bufferMessage).trim();
			
		}
	
	}	

}