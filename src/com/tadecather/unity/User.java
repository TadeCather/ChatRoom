package com.tadecather.unity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class User implements Serializable{

	/**
	 * 
	 */
	
	private int user_ID;
	private String user_passwd;
	private String user_name;
	private Date data;
	
	
	public User(int user_ID, String user_passwd, String user_name) {
		this.user_ID = user_ID;
		this.user_passwd = user_passwd;
		this.user_name = user_name;
	}
	
	
	//空白构造方法
	public User() {
	}


	public int getUser_ID() {
		return user_ID;
	}
	public void setUser_ID(int user_ID) {
		this.user_ID = user_ID;
	}
	public String getUser_passwd() {
		return user_passwd;
	}
	public void setUser_passwd(String user_passwd) {
		this.user_passwd = user_passwd;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
}
