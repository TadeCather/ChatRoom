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
	
	public ChatUI(ChatChooseUI ccu,ClientUI cu){
		
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
		this.setTitle("Chat with Somebody");
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
		
		
		
		//send °´Å¥¼àÌýÊÂ¼þ
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
		
		if(!mes.equals("")){
			cu.sendMessage(mes);
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
			
				if(cu.getData() !=  null){
					System.out.println("-------------" + cu.getData());
					messages.append(new Date() + "\n" + cu.getData() + "--Others--\n");
					
					cu.setData(null);
				}
			
			}
			
		}
		
	}
	
	
	
}










