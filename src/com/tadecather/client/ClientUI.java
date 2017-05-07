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
 * �ͻ��˵ĵ�½UI
 * @author TAD
 * 2017-5-1
 */
@SuppressWarnings("serial")
public class ClientUI extends JFrame{
	
	
	private JPanel panelName = new JPanel();
	private JPanel panelPasswd = new JPanel();
	private JPanel panelButton = new JPanel();
	private JPanel imagePanel;
	
	private JLabel JLname = new JLabel("UserName��");
	private JLabel JLpasswd = new JLabel("Passwd��    ");
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
	
	//�ظ���¼��ʶ��
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
		
		//���Ʊ���ͼƬ
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
		this.setTitle("��ӭ��½��");
		this.setResizable(false);
		this.setVisible(true);
		
		
		//���ӷ���������ʼ��socket�������socket���������������
		connect();
		
		//�����µ��̣߳�������ط������ķ�������
		new Thread(new RecThread(socket)).start();
		
		
		//��������ť����¼���Ӧ
		JBlogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//���͵�½��Ϣ
				sendLoginMessage();
				
				
				//�����߳������ڴˣ�ֱ�����յ���������Ϣ
				while(true){
					//���data��ֵ����null����ִ�в�����������ѭ��
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if(isLogin){
						
						action();
						//�����ص���Ϣ����Ϊ��
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
	
	//���ӷ���������ʼ��socket�������socket���������������
	public void connect(){
		try {
			socket = new Socket("localhost", 8888);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			isConnect = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("���ӷ�����ʧ�ܣ�");
			JOptionPane.showMessageDialog(null, "������Ϣ" , "δ�ҵ��������������������ã�",
					JOptionPane.ERROR_MESSAGE);
			
			System.out.println("��������������ã�");
		}
	}
	
	
	//�Ͽ�����
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
	//���͵�½��Ϣ
	public void sendLoginMessage(){
		String name = JTnameInput.getText();
		String passwd = JTpassswdInput.getText();
		System.out.println("Name=" + name + " passwd: " + passwd);
		String logMes = String.format("%-12s", name) + String.format("%-20s",passwd);
		System.out.println(logMes+"-");
		//�����½����10
		sendMessage((String.valueOf(10) + logMes).getBytes());
		
	}
	
	//������Ϣ
	public void sendMessage(byte[] message){
		System.out.println(message.length);
		if(isConnect){
			try {
				dos.write(message, 0, message.length);
				//ʹ��ǰ���߳�����200����
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
	
	//Ȼ��ִ�е�½�ɹ�����Ӧ�Ĳ���
	public void action(){
		
		//���ڷ���˱����Լ�socket���Լ��˺�
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
	
	
	
	
	//����һ���̣߳�������شӷ���˷�����������
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
							case 91 : {String str1 = readSomething();System.out.println("��½ʧ�ܣ�");JTpassswdInput.setText("");System.out.println(str1); break;}
							case 92 : {String str1 = readSomething();InputAccountUI.isFriendexist = 1;System.out.println(str1);break;}
							case 93 : {String str1 = readSomething();InputAccountUI.isFriendexist = 2;System.out.println("���Ѳ����ڣ�������ѡ��");System.out.println(str1);break;}
							case 94 : {String str1 = readSomething();isLogined = 1;System.out.println("������׼������������");System.out.println(str1);break;}
							case 95 : {String str1 = readSomething();isLogined = 2;System.out.println(str1);System.out.println("�벻Ҫ�ظ���¼ͬһ���˺ţ�");break;}
							
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

		//��ȡʣ�����Ϣ
		private String readSomething() throws IOException {
			byte[] bufferMessage = new byte[1024];
			dis.read(bufferMessage,0,bufferMessage.length);
			return new String(bufferMessage).trim();
			
		}
	
	}	

}