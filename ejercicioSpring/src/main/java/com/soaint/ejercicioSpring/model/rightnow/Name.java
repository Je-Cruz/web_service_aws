package com.soaint.ejercicioSpring.model.rightnow;

public class Name {
	private String first;
	private String last;
	
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getLast() {
		return last;
	}
	public void setLast(String last) {
		this.last = last;
	}
	public Name(String first, String last) {
		this.first = first;
		this.last = last;
	}
	public Name() {
	}
	@Override
	public String toString() {
		return "Name [first=" + first + ", last=" + last + "]";
	}
	
}
