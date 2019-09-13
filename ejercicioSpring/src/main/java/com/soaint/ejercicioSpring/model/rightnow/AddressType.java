package com.soaint.ejercicioSpring.model.rightnow;

/**
 * 
 * @author jcruz
 *
 */
public class AddressType {
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AddressType(int id) {
		this.id = id;
	}

	public AddressType() {
	}

	@Override
	public String toString() {
		return "AddressType [id=" + id + "]";
	}
	
}
