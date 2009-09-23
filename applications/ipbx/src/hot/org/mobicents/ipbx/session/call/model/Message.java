package org.mobicents.ipbx.session.call.model;

public class Message {
	String author;
	String text;
	public Message(String author, String text) {
		this.author = author;
		this.text = text;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
