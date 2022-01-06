package net;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import gui.Giocatore;
import gui.Master;

public class Connection<T> {
	enum Group {
		client,
		server;

		public String toString() {
			return name();
		}
	}

	// client
	protected int id;
	protected Thread readThread;
	protected String name = "player";

	// both
	protected Group owner = Group.server;

	protected ObjectInput inStream;
	protected ObjectOutputStream outStream;

	protected Queue<T> qMessageIn;
	protected Queue<T> qMessageOut;
	
	private Socket socket;

	public Connection(Group owner, Socket socket, Queue<T> qMessageIn) {
		this.owner = owner;
		this.socket = socket;
		this.qMessageIn = qMessageIn;
	}

	// relevant for server
	public void ConnectToClient(int uid) {
		if(owner == Group.server) {
			id = uid;
			try {
				inStream = new ObjectInputStream(socket.getInputStream());
				outStream = new ObjectOutputStream(socket.getOutputStream());
				readThread = new Thread(() -> { Read(); setName(String.valueOf(id)); });
				readThread.start();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	// relevant for client
	public void ConnectToServer(SocketAddress socketAddress) {
		if(owner == Group.client) {
			try {
				socket.connect(socketAddress);
				outStream = new ObjectOutputStream(socket.getOutputStream());
				inStream = new ObjectInputStream(socket.getInputStream());
				readThread = new Thread(() -> { Read(); });
				readThread.start();
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			System.out.println("Only players can connect to servers.");
		}
	}

	// both server and client
	public void Disconnect() {
		if(isConnected()) {
			try {
				socket.close();
				System.out.println("[" + id + " \"" + name + "\"] " + "Disconnected");
			} catch (IOException e) {
				System.err.println(e);
			}
		} else
			System.out.println("Already disconnected");
	}

	public boolean isConnected() {
		if(socket != null)
			return !socket.isClosed();
		else
			return false;
	}

	public void Read() {
		try {
			Message<T> in = (Message<T>)inStream.readObject();
			in.setId(id);
			qMessageIn.pushFront(in);
			if(owner == Group.server)
				Master.Notify();
			else
				Giocatore.Notify();
			Read();
		} catch (Exception e) {
			System.err.println(e);
			Disconnect();
		}
	}

	public void Send(Message<T> msg) {
		if(outStream != null) {
			try {
				outStream.writeObject(msg);
				System.out.println("[" + id + " - " + owner + "] Sent: " + msg.toString());
			} catch (Exception e) {
				System.err.println(e);
				Disconnect();
			}
		} else
			System.out.println("[ERROR] Cannot send message \"" + msg.toString() + "\". Not connected to anyone.");
	}

	public int getId() {
		return id;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		this.name = s;
	}

	public void OnMessage() {};
}