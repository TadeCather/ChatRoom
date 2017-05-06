package com.tadecather.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import java.awt.*;

public class ChatChooseUI extends JFrame{
	
	
	private JPanel personPanel = new JPanel();
	private JPanel groupPanel = new JPanel();
	private JPanel maxPanel ;
	
	private JButton personButton = new JButton("Person");
	private JButton groupButton = new JButton("Group");
	
	private Font buttonFont = new Font("Î¢ÈíÑÅºÚ", Font.BOLD, 50);
	 
	String imagePath = "d://456.jpg";
	Image image = Toolkit.getDefaultToolkit().createImage(imagePath);
	
	private ChatChooseUI ccu = this;
	private ClientUI cu = null;
	
	public ChatChooseUI(ClientUI loginUI){
		this.cu = loginUI;
		maxPanel = new JPanel(){
			protected void paintChildren(Graphics g){
				g.drawImage(image, 0, 0, this);
				super.paintChildren(g);
			}
		};
		
		Dimension preferredSize = new Dimension(300,300);
		
		personButton.setPreferredSize(preferredSize);
		personButton.setFont(buttonFont);
		personButton.setForeground(Color.BLACK);
		personButton.setContentAreaFilled(false);
		
		groupButton.setPreferredSize(preferredSize);
		groupButton.setFont(buttonFont);
		groupButton.setForeground(Color.WHITE);
		groupButton.setContentAreaFilled(false);
		
		
		personPanel.add(personButton);
		personPanel.setOpaque(false);
		groupPanel.add(groupButton);
		groupPanel.setOpaque(false);
//		personPanel.setBackground(new Color(22, 42, 60));
//		groupPanel.setBackground(new Color(22, 42, 60));
		
		
		maxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 50, 80));
		maxPanel.add(personPanel);
		maxPanel.add(groupPanel);
	
		add(maxPanel,BorderLayout.CENTER);
		
		setLocation(500, 300);
		setSize(800, 500);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cu.setVisible(true);
				dispose();
			}
				
		});
		setBackground(Color.RED);
		setTitle("Choose Type Of Chat!");
		setResizable(false);
		setVisible(true);
		
		personButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				transportThis(ccu);
				dispose();
			}
		});
		
		groupButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				transportThis(ccu);
			}
		});
	}
	
	public void transportThis(ChatChooseUI ccu){
		new  InputAccountUI(true, ccu, cu);
		dispose();
		
	}
	
	
}









