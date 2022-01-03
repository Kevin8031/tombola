package net;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection<T> {
	enum Group {
		client,
		server;

		public String toString() {
			return name();
		}
	}

	final protected int MULTICAST_PORT = 8888;
	final protected String MULTICAST_INET = "237.5.6.7";
	final protected int SERVER_PORT = 4321;
	
	// client
	protected int id;
	protected Thread readThread;

	// both
	protected Group owner = Group.server;
	protected ObjectInput inStream;
	protected ObjectOutput outStream;

	protected Queue qMessageIn;
	protected Queue qMessageOut;
	
	private Socket socket;

	public Connection(Group owner, Socket socket, Queue qMessageIn) {
		this.owner = owner;
		this.socket = socket;
		this.qMessageIn = qMessageIn;
	}

	// relevant for server
	public void ConnectToClient(int uid) {
		if(owner == Group.server)
			id = uid;
	}

	// relevant for client
	public void ConnectToServer(String host, int port) {
		if(owner == Group.client) {
			System.out.println("Attempting connection to: " + host + ":" + port);
			try {
				socket = new Socket();
				SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
				socket.connect(socketAddress);
				inStream = new ObjectInputStream(socket.getInputStream());
				outStream = new ObjectOutputStream(socket.getOutputStream());
				readThread = new Thread(() -> { Read(); });
				readThread.start();
				System.out.println("Successfully connected to: " + socket.toString());
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
				System.out.println("Disconnected");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			System.out.println("Already disconnected");
	}

	public boolean isConnected() {
		if(socket != null)
			return socket.isConnected();
		else
			return false;
	}

	public void Read() {
		try {
			Object in = inStream.readObject();
			qMessageIn.pushFront(in);
			Read();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void Send(Message<T> msg) {
		if(outStream != null) {
			try {
				outStream.writeObject(msg);
				System.out.println("[" + id + " - " + owner + "] Sent: " + msg.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			System.out.println("[ERROR] Cannot send message \"" + msg.toString() + "\". Not connected to anyone.");
	}
}