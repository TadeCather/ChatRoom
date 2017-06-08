package com.tadecather.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.border.MatteBorder;

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
	private JPanel panelButton1 = new JPanel();
	private JPanel panelButton2 = new JPanel();
	private JPanel imagePanel;
	
	private JLabel JLname = new JLabel("UserName：");
	private JLabel JLpasswd = new JLabel("Passwd：    ");
	private JTextField JTnameInput  = new JTextField(15);
	private JPasswordField JTpassswdInput  = new JPasswordField(15);
	
	private JButton JBregister = new JButton("Register");
	private JButton JBchangePasswd = new JButton("Forget the Passwd");
	private JButton JBlogin = new JButton("LOGIN");
	private JButton JBcancle = new JButton("CONCLE");
 	
	String imagePath = ".\\Source\\picture\\123.PNG";
	Image image = Toolkit.getDefaultToolkit().createImage(imagePath);
	
	
	private Socket socket = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	
	
	public boolean isLogin = false;
	
	//重复登录标识码
	private int isLogined = 0;
	
	public User userCurrent = null;
	
	public static boolean isConnect = false;
	
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
		
		panelButton2.add(JBregister);
		panelButton2.add(JBchangePasswd);
		panelButton2.setOpaque(false);
		panelButton2.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
		
		panelButton1.add(JBlogin);
		panelButton1.add(JBcancle);
		panelButton1.setOpaque(false);
		panelButton1.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 5));
		
		imagePanel.add(panelName);
		imagePanel.add(panelPasswd);
		imagePanel.add(panelButton2);
		imagePanel.add(panelButton1);
	
		imagePanel.setBorder(new MatteBorder(80, 130, 50, 100, Color.RED));
		imagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
		add(imagePanel, BorderLayout.CENTER);
		
		this.setLocation(700, 330);
		this.setSize(540, 420);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("欢迎登陆！");
		this.setResizable(false);
		this.setVisible(true);
		
		
		
		
		//对两个按钮添加事件响应
		JBlogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("buttton is presssed!");
				if(socket == null){
					connect();
				}
				
				if(isConnect){
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
				
				
			}
		});
		
		JBcancle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		} );
		
		JBregister.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//先连接服务器
				connect();
				//注册账户
				register();
				
			}
		});
		
		JBchangePasswd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				new changePasswd();
			}
		});
		
	}
	
	
	
	public void register() {
		new Register(this);
		
	}



	//连接服务器，初始化socket，并获得socket的输入输出流对象
	public void connect(){
		try {
			socket = new Socket("localhost", 8888);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			isConnect = true;
			System.out.println("连接成功！");
			//建立新的线程，用来监控服务器的返回数据
			new Thread(new RecThread(socket)).start();
			
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
			dis = null;
			dos = null;
			socket = null;
			isConnect = false;
			isLogin = false;
			JOptionPane.showMessageDialog(null, "账号已经在线，无法登陆" , "重复登陆",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	//发送登陆信息
	public void sendLoginMessage(){
		String account = JTnameInput.getText();
		@SuppressWarnings("deprecation")
		String passwd = JTpassswdInput.getText();
		String logMes = String.format("%-12s", account) + String.format("%-20s",passwd);
		System.out.println(logMes+"-");
		//请求登陆类型10
		sendMessage((String.valueOf(10) + logMes).getBytes());
		
	}
	
	//发送信息
	public void sendMessage(byte[] message){
		System.out.println();
		if(isConnect){
			try {
				dos.write(message, 0, message.length);
				System.out.println("MesLength = " + message.length + "消息已经发送");
				//使当前的线程阻塞200毫秒
				Thread.sleep(200);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}else{
				System.out.println("没有网络，无法登陆！");
		}
		
	}
	
	
	//发送文件
	public void sendFile(int mesType, File file,String friendAccount) {
		
		String fileName = file.getName();
		
		
		
		//发送文件 13P，14F，12位我的账号，12位freiend账号，12位的文件大小，接下来全部为文件名
		byte[] mes = (String.valueOf(mesType) + String.format("%-12s", userCurrent.getUserAccount())
			+ String.format("%-12s", friendAccount) + String.format("%-12s",String.valueOf(file.length()))
			+ fileName).getBytes();
		
		
		System.out.println(String.valueOf(mesType) + String.format("%-12s", userCurrent.getUserAccount())
			+ String.format("%-12s", friendAccount) + String.format("%-12s",String.valueOf(file.length()))
			+ fileName);
		
		sendMessage(mes);
		
		
		int length = 0;
		byte[] sendbyte = new byte[1024];
		FileInputStream fin;
		//发送文件的全部内容
		try {
			fin = new FileInputStream(file);
			while((length = fin.read(sendbyte,0,sendbyte.length)) >0 ){
				dos.write(sendbyte,0,length);
				dos.flush();
			}
			fin.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		  
	}
	
	
	
	
	//然后执行登陆成功后相应的步骤
	public void action(){
		
		//便于服务端保存自己socket的自己账号
		sendMessage((String.valueOf(19) + String.format("%-12s", JTnameInput.getText())+ "tadecather").getBytes());
		
		while(true){
			
			//不加的话，无法进入if，莫名奇妙,据说是编译器的优化结果，未验证！
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(isLogined == 1){

				userCurrent = new User();
				userCurrent.setUserAccount(JTnameInput.getText());
				this.setVisible(false);
				new ChatChooseUI(this);
				isLogined = 0;
				break;
			}
			if(isLogined == 2){
				disConnect();
				isLogined = 0;
				break;
			}
		}
	}

	
	//建立一个线程，用来监控从服务端发过来的数据
	private class RecThread implements Runnable{

		@SuppressWarnings("unused")
		Socket socket = null;
		public RecThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			byte[] bufferMes = new byte[1024];
			while (isConnect){
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				if(dis != null){
					try { 
						dis.read(bufferMes, 0, 2);
					
						int mesType = Integer.parseInt(new String(bufferMes).trim());
	
						System.out.println("RecMesType : " + mesType);
						
						switch(mesType){
							
							case 90 : {
								String str1 = readSomething();
								isLogin = true; 
								System.out.println(str1); 
								break;
								}
							case 91 : {
								String str1 = readSomething();
								System.out.println("登陆失败！");
								JTpassswdInput.setText("");
								System.out.println(str1);
								break;
								}
							case 92 : {
								String str1 = readSomething();
								InputAccountUI.isFriendexist = 1;
								System.out.println("好友存在，进入聊天！");
								System.out.println(str1);
								break;
								}
							case 93 : {
								String str1 = readSomething();
								InputAccountUI.isFriendexist = 2;
								System.out.println("好友不存在，请重新选择！");
								System.out.println(str1);
								JOptionPane.showMessageDialog(null, "警告信息" , "好友未找到，请输入正确账号！",
										JOptionPane.ERROR_MESSAGE);
								break;
								}
							case 94 : {
								String str1 = readSomething();
								isLogined = 1;
								System.out.println("服务器准备工作做好了");
								System.out.println(str1);
								break;
								}
							case 95 : {
								String str1 = readSomething();
								isLogined = 2;
								System.out.println(str1);
								System.out.println("请不要重复登录同一个账号！");
								break;
								}
							case 96 : {
								//注册成功信息
								showregisterSuccessful();
								System.out.println("注册成功！");
								break;
							}
							
							case 80 : {
								receiveOthersMes();
								break;
								}
							case 81 : {
								String str1 = readSomething();
								System.out.println(str1);
								ChatUI.messageowenAccount = "server"; 
								ChatUI.messagefromOther = "好友不在线";
								break;
								}
							case 82 : {
								receiveFile();break;
								}
							default : {
								System.out.println("Error Information from Server");
								}
							}
				
						
				} catch (IOException e) {
					
					isConnect = false;
//					e.printStackTrace();
					}
			
				
				}
			}
		
		}
		
		private void showregisterSuccessful() throws IOException {
			byte[] bufferMes = new byte[1024];
			dis.read(bufferMes, 0, bufferMes.length);
			String account = new String(bufferMes).trim();
			new ShowMessageFrameUI("注册账户成功！", "账号是：" + account +"  请妥善保存！" );
			disConnect();
		}

		//接受服务端传来的文件
		private void receiveFile() throws IOException {
			
			byte []  bufferMes = new byte[1024]; 
			
			dis.read(bufferMes, 0, 12);
			
	  		String ownerAccount = new String(bufferMes).trim();
	  		
	  		dis.read(bufferMes, 0, 12);
	  		
	  		int fileLength = Integer.parseInt(new String(bufferMes).trim());
	  		
	  		System.out.println(fileLength);
	  		
	  		dis.read(bufferMes, 0, bufferMes.length);
	  		String fileName = new String(bufferMes).trim();
	  		
	  		System.out.println("开始接收数据.......");

	  		FileOutputStream fos = new FileOutputStream(new File(".//clientsource//pictures//" + fileName));
	  		int length = 0;
	  		byte[] inputByte = new byte[1024];
	  		
	  		while((length = dis.read(inputByte)) != -1){
	  			System.out.println(length);
	  			
	  			//写入到指定文件夹中
				fos.write(inputByte, 0 ,length);
				fos.flush();
				fileLength -= length;
				if(fileLength <= 0){
					break;
				}
	  		}
	  
	  		fos.close();
			
	  		System.out.println("接收文件成功");
	  		ChatUI.messageowenAccount = ownerAccount;
	  		ChatUI.messagefromOther = "接受文件：" + fileName + "成功！"; 
		}

		//接受剩下的普通文本信息
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