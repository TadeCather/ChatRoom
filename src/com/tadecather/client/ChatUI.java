package com.tadecather.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

public class ChatUI extends JFrame{
	
	private JTextArea messages = new JTextArea(40, 50);
	
	
	private JTextField message = new JTextField(30);
	private JButton send = new JButton("SEND");
	
	private JScrollPane scroll = new JScrollPane(messages);
	
	private JPanel sendMessage = new JPanel();
	
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
				
				send();

				
			}
		});
		
		message.addKeyListener(new KeyMonitor());
		
		new Thread(new CheckMessage()).start();
		
	}



	protected void send() {
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
	
//	
	private class KeyMonitor extends KeyAdapter{
		
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				send();
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
					messages.append("From " + messageowenAccount +  new Date() + "\n" + messagefromOther + "\n");
					
					messagefromOther = null;
					messageowenAccount = null;
				}
			
					

			
			}
			
		}
		
	}
	
	
	
}










