package com.tadecather.unity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class User implements Serializable{

	/**
	 * User,主要存储用户的基本信息
	 * account，passwd，name，signuptime。
	 */
	
	private String userAccount;
	private String userPasswd;
	private String userName;
	private Date signUpTime;
	
	
	public User(String userAccount, String userPasswd, String userName, Date signUpTime) {
		this.userAccount = userAccount;
		this.userPasswd = userPasswd;
		this.userName = userName;
		this.signUpTime = signUpTime;
	}
	
	//空白构造方法
	public User(){
		
	}
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserPasswd() {
		return userPasswd;
	}
	public void setUserPasswd(String userPasswd) {
		this.userPasswd = userPasswd;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getSignUpTime() {
		return signUpTime;
	}
	public void setSignUpTime(Date signUpTime) {
		this.signUpTime = signUpTime;
	}
	
	
	
	
	
}
