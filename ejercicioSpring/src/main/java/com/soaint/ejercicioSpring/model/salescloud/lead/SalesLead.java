package com.soaint.ejercicioSpring.model.salescloud.lead;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesLead {
	@JsonProperty("ContactPartyNumber")
	private int contactPartyNumber;
	
	@JsonIgnore
	private int id;
	
	public int getContactPartyNumber() {
		return contactPartyNumber;
	}
	public void setContactPartyNumber(int contactPartyNumber) {
		this.contactPartyNumber = contactPartyNumber;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public SalesLead(int contactPartyNumber) {
		this.contactPartyNumber = contactPartyNumber;
	}
	public SalesLead() {
	}
	
}
