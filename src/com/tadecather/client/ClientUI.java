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
	
	public boolean isDataChange = false;
	
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
		JTnameInput.setText("14130130229");
		panelName.setOpaque(false);
		
		panelPasswd.add(JLpasswd);
		panelPasswd.add(JTpassswdInput);
		JTpassswdInput.setText("030290");
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
				while(isConnect){
					//如果data的值不是null，就执行操作，并跳出循环
					if(data != null){
						action();
						//将返回的消息重置为空
						data = null;
						
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
		String logMes = name + passwd;
		
		sendMessage(logMes);
	}
	
	//发送信息
	public void sendMessage(String mes){
		if(isConnect){
			try {
				
				
				dos.writeUTF(mes);
				//使当前的线程阻塞500毫秒
				
				Thread.sleep(100);
				
			} catch (IOException e) {
				System.out.println("登陆失败！");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//判端数据的正确性，然后执行相应的步骤
	public void action(){
		if(data.equals("登陆成功，欢迎你！")){
			
			this.setVisible(false);
			new ChatChooseUI(this);
			
		}else{
			JTpassswdInput.setText("");
			System.out.println("登陆失败！");
		}
	}
	
	
	
	
	//建立一个线程，用来监控从服务端发过来的数据
	private class RecThread implements Runnable{

		Socket socket = null;
		public RecThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			
			while (isConnect){
				try { 
					data = dis.readUTF();
					System.out.println("我是客户端，服务器端的响应是：" + data);
					isDataChange = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	
}
