package com.soaint.ejercicioSpring.model.eloqua;

import java.util.ArrayList;

public class ContactsEloqua {
	private String firstName;
	private String lastName;
	private String emailAddress;
	ArrayList<Elements> elements;
	
	public ArrayList<Elements> getElements() {
		return elements;
	}

	public void setElements(ArrayList<Elements> elements) {
		this.elements = elements;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public ContactsEloqua(String firstName, String lastName, String emailAddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}
	public ContactsEloqua() {
	}
	
}
