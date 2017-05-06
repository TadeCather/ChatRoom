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
	
	public boolean isDataChange = false;
	
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
				while(isConnect){
					//���data��ֵ����null����ִ�в�����������ѭ��
					if(data != null){
						action();
						//�����ص���Ϣ����Ϊ��
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
		String logMes = name + passwd;
		
		sendMessage(logMes);
	}
	
	//������Ϣ
	public void sendMessage(String mes){
		if(isConnect){
			try {
				
				
				dos.writeUTF(mes);
				//ʹ��ǰ���߳�����500����
				
				Thread.sleep(100);
				
			} catch (IOException e) {
				System.out.println("��½ʧ�ܣ�");
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//�ж����ݵ���ȷ�ԣ�Ȼ��ִ����Ӧ�Ĳ���
	public void action(){
		if(data.equals("��½�ɹ�����ӭ�㣡")){
			
			this.setVisible(false);
			new ChatChooseUI(this);
			
		}else{
			JTpassswdInput.setText("");
			System.out.println("��½ʧ�ܣ�");
		}
	}
	
	
	
	
	//����һ���̣߳�������شӷ���˷�����������
	private class RecThread implements Runnable{

		Socket socket = null;
		public RecThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			
			while (isConnect){
				try { 
					data = dis.readUTF();
					System.out.println("���ǿͻ��ˣ��������˵���Ӧ�ǣ�" + data);
					isDataChange = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	
	
	
}
