package com.tadecather.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

public class ChatUI extends JFrame{

	private static final long serialVersionUID = 1L;


	private JTextArea messages = new JTextArea(40, 50);
	
	
	private JTextField message = new JTextField(25);
	private JButton send = new JButton("SEND");
	private JButton chooseFile = new JButton("CHoose File");
	
	private JScrollPane scroll = new JScrollPane(messages);
	
	private JPanel sendMessage = new JPanel();
	
	@SuppressWarnings("unused")
	private ChatChooseUI ccu = null;
	
	private ClientUI cu = null;
	
	private String friendAccount;
	
	public static String messageowenAccount = null;
	public static String messagefromOther = null;
	
	public ChatUI(String friendAccount,ChatChooseUI ccu,ClientUI cu){
		this.friendAccount = friendAccount;
		this.ccu = ccu;
		this.cu = cu;
		
		
		messages.setLineWrap(true);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sendMessage.add(message);
		sendMessage.add(chooseFile);
		sendMessage.add(send,BorderLayout.WEST);
		this.add(scroll, BorderLayout.NORTH);
		this.add(sendMessage, BorderLayout.SOUTH);		
		
		
		this.setSize(500, 800);
		this.setLocation(500,175);
		this.setTitle("Chat with " + friendAccount);
		this.setResizable(false);
		this.setVisible(true);
		
		this.addKeyListener(new KeyMonitor());
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				ccu.setVisible(true);
				dispose();
			}
			
		});
		
		
		
		//send 按钮监听事件
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				sendText();

				
			}
		});
		
		//chosefile 按钮监听事件
		chooseFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = choosefile();
				
				try {
					
					BufferedImage sourceimage = ImageIO.read(new FileInputStream(file));
					
					//如果是图片。。。。。
					if(sourceimage != null){
						messages.append("图片即将被发送！" + file.getAbsolutePath() + "\n");
						Image image = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
						System.out.println(file.getName());
						new PictureJFrame(sourceimage.getWidth(),sourceimage.getHeight(), image, file);
					}else{
						messages.append("文件即将被发送！" + file.getAbsolutePath()+ "\n");
						sendFile(file);
					}
					
					
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			}

		});
		
		message.addKeyListener(new KeyMonitor());
		
		new Thread(new CheckMessage()).start();
		
	}

	//发送普通文本消息
	protected void sendText() {
		String mes = message.getText();
		byte[] mesText = new byte[1024];
		if(!mes.equals("")){
			
			//普通信息 12
			mesText = ("12" + String.format("%-12s", cu.userCurrent.getUserAccount())
				+ String.format("%-12s",friendAccount) + mes).getBytes();
			
			cu.sendMessage(mesText);
	
			messages.append(new Date() + "\n" + mes + "\n" +"\n");
		}
		message.setText("");
		
		
	}
	
	
	//发送图片和文件
	
	private void sendFile(File file) {
		
		cu.sendFile(13, file, friendAccount);
		
	}
	
	private void sendPicture(File picture){
		cu.sendFile(14, picture, friendAccount);
	}
	
	
	
	//chooseFile 选择发送的文件
	private File choosefile() {
		JFileChooser chooser = new JFileChooser("D:\\");
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int ret = chooser.showDialog(new JLabel(), "选择要发送的文件");
		if (ret == JFileChooser.APPROVE_OPTION) {
		        File file = chooser.getSelectedFile();
		        return file;
		}else
			return null;
	}
	
     //监听键盘的ENTER键
	private class KeyMonitor extends KeyAdapter{
		
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				sendText();
			}
		}
	}
	
	public class CheckMessage implements Runnable{

		@Override
		public void run() {
			while(ClientUI.isConnect){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if((messagefromOther != null)&&(messageowenAccount != null)){
					System.out.println("-------------" + messagefromOther);
					messages.append("From " + messageowenAccount + " " +  new Date() + "\n" + messagefromOther + "\n");
					
					//将消息重新设置为null
					messagefromOther = null;
					messageowenAccount = null;
				}

			}
			
		}
		
	}
	
	class PictureJFrame extends JFrame{
		
		
		private static final long serialVersionUID = 1L;
		
		private Font buttonFont = new Font("微软雅黑", Font.BOLD, 18);
		File picture = null;
		public PictureJFrame(int width,int height, Image image,File picture) {
			this.picture = picture;
			JPanel imagePanel  = new JPanel(){
				
				private static final long serialVersionUID = 1L;

				protected void paintChildren(Graphics g){
					g.drawImage(image, 0, 0, this);
					super.paintChildren(g);
				}
			};
			
			JButton canel = new JButton("CANEL");
			JButton sendPicture = new JButton("SEND");
			JPanel buttonsPanel = new JPanel();
			
			buttonsPanel.add(sendPicture,BorderLayout.WEST);
			buttonsPanel.add(canel, BorderLayout.EAST);
			Dimension preferredSize = new Dimension(100,50);
			sendPicture.setFont(buttonFont);
			canel.setFont(buttonFont);
			
			canel.setPreferredSize(preferredSize);
			sendPicture.setPreferredSize(preferredSize);
			canel.setForeground(Color.WHITE);
			sendPicture.setForeground(Color.WHITE);
			sendPicture.setContentAreaFilled(false);
			canel.setContentAreaFilled(false);
			buttonsPanel.setOpaque(false);
			imagePanel.add(buttonsPanel,BorderLayout.SOUTH);
			this.add(imagePanel);
			
			this.setLocation(300, 100);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setUndecorated(true);
			this.setSize(width, height);
			this.setResizable(false);
			this.setVisible(true);
			
			canel.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					
				}
			});
			sendPicture.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					//发送图片
					sendPicture(picture);
					
					dispose();
					System.out.println("发送图片");
				}

				
				
			});
			
		}
		
	}
	
	
	
}










