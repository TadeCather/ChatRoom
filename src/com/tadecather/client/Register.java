package com.tadecather.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tadecather.tools.GBCHelper;

public class Register extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	JLabel JLwelcom = null;
	
	JLabel JLuserName = null;
	JLabel JLuserPasswd = null;
	JLabel JLconfirmPasswd = null;
	JLabel JLEmail = null;
	
	JTextField JTuserName = null;
	JTextField JTuserPasswd = null;
	JTextField JTconfirmPasswd = null;
	JTextField JTEmail = null;
	
	JButton register = null;
	JButton cancel = null;
	
	JPanel panel = null;
	

	public Register(ClientUI cu){
		JLwelcom = new JLabel("Welcom Register ChatRoom");
		JLwelcom.setFont(new Font("", 1, 35));
		
		JLuserName = new JLabel("UserName  ");
		JLuserName.setBackground(Color.green);
		JLuserPasswd = new JLabel("Passwd  ");
		JLconfirmPasswd = new JLabel("ConfirmPasswd  ");
		JLEmail = new JLabel("Email  ");
		
		JTuserName = new JTextField(20);
		JTuserPasswd = new JTextField(20);
		JTconfirmPasswd = new JTextField(20);
		JTEmail = new JTextField(20);
		
		register = new JButton("Register");
		cancel = new JButton("Cancel");
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
	
		panel.add(JLwelcom, new GBCHelper(0,  0, 2, 3).setAnchor(GBCHelper.CENTER)
				.setInsets(5).setIpad(10, 10).setWeight(0, 5));
		
		panel.add(JLuserName, new GBCHelper(0, 3, 1, 1).setAnchor(GBCHelper.EAST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		panel.add(JTuserName, new GBCHelper(1, 3, 1, 1).setAnchor(GBCHelper.WEST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		
		panel.add(JLuserPasswd, new GBCHelper(0, 4, 1, 1).setAnchor(GBCHelper.EAST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		panel.add(JTuserPasswd, new GBCHelper(1, 4, 1, 1).setAnchor(GBCHelper.WEST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		
		panel.add(JLconfirmPasswd, new GBCHelper(0, 5, 1, 1).setAnchor(GBCHelper.EAST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		panel.add(JTconfirmPasswd, new GBCHelper(1, 5, 1, 1).setAnchor(GBCHelper.WEST)
				.setInsets(5).setIpad(10, 10).setWeight(0, 1));
		
		panel.add(register, new GBCHelper(0, 6, 1, 1).setAnchor(GBCHelper.EAST)
				.setInsets(5).setIpad(10, 10).setWeight(100, 5));
		panel.add(cancel, new GBCHelper(1, 6, 1, 1).setAnchor(GBCHelper.WEST)
				.setInsets(50).setIpad(10, 10).setWeight(100, 5));
		
		add(panel);
		
		setLocation(630, 230);
		setSize(600, 540);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		setTitle("Register Account");
		setMinimumSize(new Dimension(540, 480));
		setVisible(true);
		
		
		
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String userName = JTuserName.getText();
				String passwd = JTuserPasswd.getText();
				String confirmpwd = JTconfirmPasswd.getText();
				if(!passwd.equals(confirmpwd)){
					JTconfirmPasswd.setText("");
					return;
				}
				byte [] message = (String.valueOf(15) + String.format("%-12s", 1) + 
						String.format("%-20s",passwd) + userName).getBytes();
				
				cu.sendMessage(message);
				
				
				//showAccount(account);
				dispose();
			}

			
			
		});
		
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});
	}
	



}