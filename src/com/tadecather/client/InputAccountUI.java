package com.tadecather.client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

class InputAccountUI extends JFrame{
	
	protected static final ChatChooseUI ChatChooseUI = null;

	
	
	private JTextField accountInput = new JTextField(15);
	private JButton sureAccount = new JButton("Sure");
	private JButton concle = new JButton("concle");
	private JPanel buttonPanel = new JPanel();
	private JPanel allPanel = new JPanel(); 
	
	private ChatChooseUI ccu = null;
	private ClientUI cu = null;
	
	public static int isFriendexist = 0;
	
	public InputAccountUI(boolean isPerson,ChatChooseUI ccu,ClientUI cu){
		
		this.ccu = ccu;
		this.cu = cu;
		
		accountInput.setText("10000");
		sureAccount.setBackground(Color.gray);
		concle.setBackground(Color.gray);
		buttonPanel.add(sureAccount);
		buttonPanel.add(concle);
		buttonPanel.setOpaque(false);
		
		
		allPanel.add(accountInput);
		allPanel.add(buttonPanel);
		allPanel.setBorder(new MatteBorder(20,0, 0, 0 , Color.darkGray ));
		allPanel.setBackground(Color.DARK_GRAY);
		this.add(allPanel);
		
		if(isPerson){
			this.setTitle("选择聊天对方ID");
		}else{
			this.setTitle("选择群聊的群ID");
		}
		this.setLayout(new GridLayout(1, 2));
		this.setLocation(700, 330);
		this.setSize(300, 200);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				dispose();
				ccu.setVisible(true);
				
			}
		});
		sureAccount.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String account = accountInput.getText();
				
				String mesRequestWithFriend = "11" + String.format("%-12s", cu.userCurrent.getUserAccount())
					+String.format("%-12s", account);
				byte[] mesRWF = mesRequestWithFriend.getBytes();
				
				cu.sendMessage(mesRWF);
				
				while(isFriendexist != 0){
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					if(isFriendexist == 1){
						//采取的活动
						action(account);
						isFriendexist = 0;
						break;
					}
					if(isFriendexist == 2){
						accountInput.setText("");
						isFriendexist = 0;
						break;
					}
					
				}
				
				
			}
		});
		
		concle.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ccu.setVisible(true);
				dispose();
			}
		});
	
	}
	
	
	//判断活动
	public void action(String friendAccount){
		new ChatUI(friendAccount, ccu, cu);
		setVisible(false);
		ccu.setVisible(false);
	}
	
	
}
