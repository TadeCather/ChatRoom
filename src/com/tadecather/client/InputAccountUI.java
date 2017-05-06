package com.tadecather.client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	
	public InputAccountUI(boolean isPerson,ChatChooseUI ccu,ClientUI cu){
		
		this.ccu = ccu;
		this.cu = cu;
		
		accountInput.setText("110120130");
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
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		sureAccount.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String account = accountInput.getText();
				
				cu.sendMessage(account);
				
				while(cu.getData() != null){
					
					//采取的活动
					action();
					
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
	public void action(){
		if(cu.getData().equals("账户存在！进入聊天！")){
			cu.setData(null);
			new ChatUI(ccu, cu);
			setVisible(false);
			ccu.setVisible(false);
		}else{
			accountInput.setText("");
		}
	}
	
	
}
