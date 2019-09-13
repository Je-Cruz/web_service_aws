package com.soaint.ejercicioSpring.model.rightnow;

/**
 * 
 * @author jcruz
 *
 */
public class Emails {
	private String address;
	private AddressType addressType;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public AddressType getAddressType() {
		return addressType;
	}
	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}
	public Emails(String address, AddressType addressType) {
		this.address = address;
		this.addressType = addressType;
	}
	public Emails() {
	}
	@Override
	public String toString() {
		return "Emails [address=" + address + ", addressType=" + addressType.toString() + "]";
	}
	
}
