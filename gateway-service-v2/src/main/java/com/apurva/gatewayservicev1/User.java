package com.apurva.gatewayservicev1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer userIdInteger;
	
	@Email
	@NotEmpty
	@Column(unique = true)
	private String emailString;
	
	private String passwordString;
	private String firstNameString;
	private String lastNameString;
	private boolean isSeller;
	private String phoneNumberString;
	
	public User() {
		
	}
	
	public User(Integer userIdInteger, @Email @NotEmpty String emailString, String passwordString,
			String firstNameString, String lastNameString, boolean isSeller, String phoneNumberString) {
		super();
		this.userIdInteger = userIdInteger;
		this.emailString = emailString;
		this.passwordString = passwordString;
		this.firstNameString = firstNameString;
		this.lastNameString = lastNameString;
		this.isSeller = false;
		this.phoneNumberString = phoneNumberString;
	}

	public boolean isSeller() {
		return isSeller;
	}


	public void setSeller(boolean isSeller) {
		this.isSeller = isSeller;
	}


	public Integer getUserIdInteger() {
		return userIdInteger;
	}

	public void setUserIdInteger(Integer userIdInteger) {
		this.userIdInteger = userIdInteger;
	}

	public String getEmailString() {
		return emailString;
	}

	public void setEmailString(String emailString) {
		this.emailString = emailString;
	}

	public String getPasswordString() {
		return passwordString;
	}

	public void setPasswordString(String passwordString) {
		this.passwordString = passwordString;
	}

	public String getFirstNameString() {
		return firstNameString;
	}

	public void setFirstNameString(String firstNameString) {
		this.firstNameString = firstNameString;
	}

	public String getLastNameString() {
		return lastNameString;
	}

	public void setLastNameString(String lastNameString) {
		this.lastNameString = lastNameString;
	}

	public String getPhoneNumberString() {
		return phoneNumberString;
	}

	public void setPhoneNumberString(String phoneNumberString) {
		this.phoneNumberString = phoneNumberString;
	}
	
	
	
	
}

