package com.soaint.ejercicioSpring.model.rightnow;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author jcruz
 *
 */
public class ContactsRightNow {
	private Name name;
	private Emails emails;
	@JsonIgnore
	private  ArrayList<Items> items;
	
	public ArrayList<Items> getItems() {
		return items;
	}
	public void setItems(ArrayList<Items> items) {
		this.items = items;
	}
	public Name getName() {
		return name;
	}
	public void setName(Name name) {
		this.name = name;
	}
	public Emails getEmails() {
		return emails;
	}
	public void setEmails(Emails emails) {
		this.emails = emails;
	}
	public ContactsRightNow(Name name, Emails emails) {
		this.name = name;
		this.emails = emails;
	}
	public ContactsRightNow(ArrayList<Items> items) {
		this.items = items;
	}
	public ContactsRightNow() {
	}
	@Override
	public String toString() {
		return "ContactRN [name=" + name.toString() + ", emails=" + emails.toString() + "]";
	}
	
	
}
