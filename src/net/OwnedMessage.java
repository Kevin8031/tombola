package net;

import java.io.Serializable;

public class OwnedMessage<T> implements Serializable {
	private Message<T> msg;
	private Connection<T> remote;

	public OwnedMessage() {
		msg = new Message<T>();
		remote = null;
	} 

	public OwnedMessage(Message<T> msg) {
		this.msg = msg;
		remote = null;
	}

	@SafeVarargs
	public <I> OwnedMessage(T id, I... body) {
		this.msg = new Message<T>(id, body);
		remote = null;
	}

	public OwnedMessage(Connection<T> remote) {
		msg = new Message<T>();
		this.remote = remote;
	}

	public OwnedMessage(Message<T> msg, Connection<T> remote) {
		this.msg = msg;
		this.remote = remote;
	}

	@SafeVarargs
	public final <I> void Add(I... body) {
		msg.Add(body);
	}

	public Message<T> getMsg() {
		return msg;
	}

	public void SetMsg(Message<T> msg) {
		this.msg = msg;
	}

	public Connection<T> getRemote() {
		return remote;
	}

	public void setRemote(Connection<T> remote) {
		this.remote = remote;
	}
}