package net;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;

public class Server<T> {
	private ServerSocket serverSocket;
	private MulticastSocket multicastSocket;
	private Thread serverThread;
	private Thread lanThread;
	private boolean openToLan;
	private String serverName;

	private Deque<Connection<T>> connections;
	private Queue<T> qMessageIn;
	private int idCounter;
	public Server() {
		connections = new LinkedList<Connection<T>>();
		qMessageIn = new Queue<T>();
		idCounter = 0;
	}
	
	public void Start(boolean bWait) {
		if(serverSocket == null) {
			try {
				serverSocket = new ServerSocket(Common.SERVER_PORT);
				serverThread = new Thread(() -> Accept());
				serverThread.setName("serverThread");
				serverThread.start();

				System.out.println("[SERVER] Server started!");
				System.out.println("[SEVRER] Name: " + serverName);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("[SERVER] Server already started.");
		}
	}

	public void Stop() {
		if(serverSocket != null) {
			try {
				
				serverThread.join(1);
				serverSocket.close();
				
				System.out.println("[SERVER] Server Stopped");
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("[SERVER] Cannot stop server. Server already stopped!");
		}
		StopOpenToLan();
	}

	public boolean isServerStarted() {
		return !serverSocket.isClosed();
	}

	private void Accept() {
		try {
			System.out.println("[SERVER] Waiting for client to connect...");
			Socket s = serverSocket.accept();
			Connection<T> newconn = new Connection<T>(Connection.Group.server, s, qMessageIn);
			connections.addLast(newconn);
			connections.getLast().ConnectToClient(idCounter++);

			System.out.println("[NEW CLIENT] Client connected: " + newconn.getSocket().toString());
			System.out.println("[SERVER] No. of clients: " + connections.size());
			Accept();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void StartOpenToLan() {
		if(lanThread == null) {
			lanThread = new Thread(() -> OpenToLan());
			lanThread.setName("lanThread");
			lanThread.start();
			openToLan = true;
			if(!isServerStarted()) {
				System.out.println("[LAN SEARCH] Server was not started. Starting...");
				Start(true);
			}
		} else {
			System.out.println("[LAN SEARCH] Already started.");
		}
	}

	private void OpenToLan() {
		try {
			multicastSocket = new MulticastSocket();
			InetAddress inet = InetAddress.getByName(Common.MULTICAST_INET);
			multicastSocket.joinGroup(inet);
			System.out.println("[SERVER] Server opened to lan.");
			Message<T> msg = new Message<T>(MessageType.LAN_SERVER_DISCOVEY);
			msg.Add(serverName, serverSocket.getLocalPort());

			while (openToLan) {
				DatagramPacket send = new DatagramPacket(msg.toString().getBytes(), msg.toString().length(), inet, Common.MULTICAST_PORT);
				multicastSocket.send(send);
				System.out.println("[LAN SEARCH] Sent: " + msg);
				Thread.sleep(5000);
			}
			System.out.println("[SERVER] Not visible on lan.");
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("[SERVER] Cannot open server to lan.");
		}
	}

	public void StopOpenToLan() {
		if(multicastSocket != null) {
			openToLan = false;
			try {
				multicastSocket.close();
				multicastSocket = null;
				System.out.println("[LAN SEARCH] Waiting for thread to finish execution...");
				lanThread.join();
				lanThread = null;
			} catch (Exception e) {
				System.err.println(e);
			}
		} else
			System.out.println("[SERVER] Lan visibility already stopped");
	}

	public void MessageClient(Connection<T> client, Message<T> msg) {
		if(client != null && client.isConnected()) {
			client.Send(msg);
		} else {
			client.Disconnect();

			connections.remove(client);
		}
	}

	public void MessageAllClients(Message<T> msg, Connection<T> ignoreClient) {
		boolean invalidClient = false;
		for (Connection<T> c : connections) {
			if(c != ignoreClient)
				if(c != null && c.isConnected())
					c.Send(msg);
				else
					invalidClient = true;
		}

		if(invalidClient)
			connections.remove(null);
	}

	public Connection<T> getClient(int id) {
		for (Connection<T> connection : connections)
			if(connection.getId() == id)
				return connection;

		return null;
	}

	public Deque<Connection<T>> getClients() {
		return connections;
	}

	public Queue<T> Incoming() {
		return qMessageIn;
	}

	public Deque<Connection<T>> getConnections() {
		return connections;
	}
}