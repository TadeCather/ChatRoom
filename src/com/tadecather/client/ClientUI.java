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
 * �ͻ��˵ĵ�½UI
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
	
	private JLabel JLname = new JLabel("UserName��");
	private JLabel JLpasswd = new JLabel("Passwd��    ");
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
	
	//�ظ���¼��ʶ��
	private int isLogined = 0;
	
	public User userCurrent = null;
	
	public static boolean isConnect = false;
	
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
		this.setTitle("��ӭ��½��");
		this.setResizable(false);
		this.setVisible(true);
		
		
		
		
		//��������ť����¼���Ӧ
		JBlogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("buttton is presssed!");
				if(socket == null){
					connect();
				}
				
				if(isConnect){
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
				//�����ӷ�����
				connect();
				//ע���˻�
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



	//���ӷ���������ʼ��socket�������socket���������������
	public void connect(){
		try {
			socket = new Socket("localhost", 8888);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			isConnect = true;
			System.out.println("���ӳɹ���");
			//�����µ��̣߳�������ط������ķ�������
			new Thread(new RecThread(socket)).start();
			
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
			dis = null;
			dos = null;
			socket = null;
			isConnect = false;
			isLogin = false;
			JOptionPane.showMessageDialog(null, "�˺��Ѿ����ߣ��޷���½" , "�ظ���½",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	//���͵�½��Ϣ
	public void sendLoginMessage(){
		String account = JTnameInput.getText();
		@SuppressWarnings("deprecation")
		String passwd = JTpassswdInput.getText();
		String logMes = String.format("%-12s", account) + String.format("%-20s",passwd);
		System.out.println(logMes+"-");
		//�����½����10
		sendMessage((String.valueOf(10) + logMes).getBytes());
		
	}
	
	//������Ϣ
	public void sendMessage(byte[] message){
		System.out.println();
		if(isConnect){
			try {
				dos.write(message, 0, message.length);
				System.out.println("MesLength = " + message.length + "��Ϣ�Ѿ�����");
				//ʹ��ǰ���߳�����200����
				Thread.sleep(200);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				}
			}else{
				System.out.println("û�����磬�޷���½��");
		}
		
	}
	
	
	//�����ļ�
	public void sendFile(int mesType, File file,String friendAccount) {
		
		String fileName = file.getName();
		
		
		
		//�����ļ� 13P��14F��12λ�ҵ��˺ţ�12λfreiend�˺ţ�12λ���ļ���С��������ȫ��Ϊ�ļ���
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
		//�����ļ���ȫ������
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
	
	
	
	
	//Ȼ��ִ�е�½�ɹ�����Ӧ�Ĳ���
	public void action(){
		
		//���ڷ���˱����Լ�socket���Լ��˺�
		sendMessage((String.valueOf(19) + String.format("%-12s", JTnameInput.getText())+ "tadecather").getBytes());
		
		while(true){
			
			//���ӵĻ����޷�����if��Ī������,��˵�Ǳ��������Ż������δ��֤��
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

	
	//����һ���̣߳�������شӷ���˷�����������
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
								System.out.println("��½ʧ�ܣ�");
								JTpassswdInput.setText("");
								System.out.println(str1);
								break;
								}
							case 92 : {
								String str1 = readSomething();
								InputAccountUI.isFriendexist = 1;
								System.out.println("���Ѵ��ڣ��������죡");
								System.out.println(str1);
								break;
								}
							case 93 : {
								String str1 = readSomething();
								InputAccountUI.isFriendexist = 2;
								System.out.println("���Ѳ����ڣ�������ѡ��");
								System.out.println(str1);
								JOptionPane.showMessageDialog(null, "������Ϣ" , "����δ�ҵ�����������ȷ�˺ţ�",
										JOptionPane.ERROR_MESSAGE);
								break;
								}
							case 94 : {
								String str1 = readSomething();
								isLogined = 1;
								System.out.println("������׼������������");
								System.out.println(str1);
								break;
								}
							case 95 : {
								String str1 = readSomething();
								isLogined = 2;
								System.out.println(str1);
								System.out.println("�벻Ҫ�ظ���¼ͬһ���˺ţ�");
								break;
								}
							case 96 : {
								//ע��ɹ���Ϣ
								showregisterSuccessful();
								System.out.println("ע��ɹ���");
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
								ChatUI.messagefromOther = "���Ѳ�����";
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
			new ShowMessageFrameUI("ע���˻��ɹ���", "�˺��ǣ�" + account +"  �����Ʊ��棡" );
			disConnect();
		}

		//���ܷ���˴������ļ�
		private void receiveFile() throws IOException {
			
			byte []  bufferMes = new byte[1024]; 
			
			dis.read(bufferMes, 0, 12);
			
	  		String ownerAccount = new String(bufferMes).trim();
	  		
	  		dis.read(bufferMes, 0, 12);
	  		
	  		int fileLength = Integer.parseInt(new String(bufferMes).trim());
	  		
	  		System.out.println(fileLength);
	  		
	  		dis.read(bufferMes, 0, bufferMes.length);
	  		String fileName = new String(bufferMes).trim();
	  		
	  		System.out.println("��ʼ��������.......");

	  		FileOutputStream fos = new FileOutputStream(new File(".//clientsource//pictures//" + fileName));
	  		int length = 0;
	  		byte[] inputByte = new byte[1024];
	  		
	  		while((length = dis.read(inputByte)) != -1){
	  			System.out.println(length);
	  			
	  			//д�뵽ָ���ļ�����
				fos.write(inputByte, 0 ,length);
				fos.flush();
				fileLength -= length;
				if(fileLength <= 0){
					break;
				}
	  		}
	  
	  		fos.close();
			
	  		System.out.println("�����ļ��ɹ�");
	  		ChatUI.messageowenAccount = ownerAccount;
	  		ChatUI.messagefromOther = "�����ļ���" + fileName + "�ɹ���"; 
		}

		//����ʣ�µ���ͨ�ı���Ϣ
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