package com.soaint.ejercicioSpring.model.salescloud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactsOSC {
	@JsonProperty("FirstName")
	private String firstName;
	
	@JsonProperty("LastName")
	private String lastName;
	
	@JsonProperty("EmailAddress")
	private String emailAddress;
	
	@JsonIgnore
	private int partyNumber;
	
	@JsonProperty("FirstName")
	public String getFirstName() {
		return firstName;
	}
	
	@JsonProperty("FirstName")
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@JsonProperty("LastName")
	public String getLastName() {
		return lastName;
	}
	
	@JsonProperty("LastName")
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@JsonProperty("EmailAddress")
	public String getEmailAddress() {
		return emailAddress;
	}
	
	@JsonProperty("EmailAddress")
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public int getPartyNumber() {
		return partyNumber;
	}

	public void setPartyNumber(int partyNumber) {
		this.partyNumber = partyNumber;
	}
	
	
	public ContactsOSC(String firstName, String lastName, String emailAddress) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
	}
	
	
	public ContactsOSC(int partyNumber) {
		this.partyNumber = partyNumber;
	}
	
	public ContactsOSC() {
	}
	
	@Override
	public String toString() {
		return "ContactsOSC [firstName=" + firstName + ", lastName=" + lastName + ", emailAddress=" + emailAddress
				+ "]";
	}
		
}
