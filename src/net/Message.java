package net;

import java.io.Serializable;
import java.util.ArrayList;

public class Message<T> implements Serializable {
	private MessageHead<T> head;
	private ArrayList<Object> body;
	private int id;

	public Message() {
		this.head = new MessageHead<T>();
		this.body = new ArrayList<Object>();
	}

	public Message(MessageHead<T> head) {
		this.head = head;
		this.body = new ArrayList<Object>();
	}

	public Message(T id) {
		this.head = new MessageHead<T>(id);
		this.body = new ArrayList<Object>();
	}

	public <I> Message(T id, I... body) {
		this.head = new MessageHead<T>(id);
		this.body = new ArrayList<Object>();
		Add(body);
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

	public T getHeadId() {
		return head.id;
	}

	public void setHeadId(T id) {
		head.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

class MessageHead<T> implements Serializable {
	protected T id;
	protected int size;

	protected MessageHead(T id) {
		this.id = id;
	}

	protected MessageHead() {}
}