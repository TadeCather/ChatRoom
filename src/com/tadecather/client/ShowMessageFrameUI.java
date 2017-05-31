package com.tadecather.client;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ShowMessageFrameUI extends JFrame {
	
	public ShowMessageFrameUI(String title,String message){
		
		JLabel JLmessage = new JLabel(message);
		add(JLmessage,BorderLayout.CENTER);
		setTitle(title);
		setSize(500,400);
		setLocation(500,300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
