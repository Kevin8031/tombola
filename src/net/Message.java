package net;

import java.util.ArrayList;

public class Message<T> {
	private MessageHead<T> head;
	private ArrayList<Object> body;

	public Message() {
		this.head = new MessageHead<T>();
		this.body = new ArrayList<Object>();
	}

	public Message(MessageHead<T> head) {
		this.head = head;
	}

	public <I> Message(MessageHead<T> head, I... body) {
		this.head = head;
		this.body = new ArrayList<Object>();
		Add(body);
	}

	public <I> Message(I... body) {
		this.head = new MessageHead<T>();
		this.body = new ArrayList<Object>();
		Add(body);
	}

	public <I> void Add (I... body) {
		for (I element : body) {
			this.body.add(element);
			UpdateSize();
		}
	}

	public <I> I Get(I obj) {
		if(body.size() > 0) {
			I element = (I)body.get(0);
			body.remove(0);
			UpdateSize();
			return element;
		} else
			return null;
	}

	public ArrayList<Object> GetAll() {
		return body;
	}

	private void UpdateSize() {
		head.size = body.size();
	}

	public int getSize() {
		return head.size;
	}

	public T getHead() {
		return head.id;
	}
}

class MessageHead<T> {
	protected T id;
	protected int size;
}