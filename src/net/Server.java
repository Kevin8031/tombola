package net;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;

public class Server<T> {
	protected ServerSocket serverSocket;
	private Thread serverThread;
	protected String serverName;

	private Deque<Connection<T>> connections;
	private TsQueue<T> qMessageIn;
	private int idCounter;
	public Server() {
		connections = new LinkedList<Connection<T>>();
		qMessageIn = new TsQueue<T>();
		idCounter = 0;
	}
	
	public void Start() {
		if(serverSocket == null) {
			try {
				serverSocket = new ServerSocket(Common.SERVER_PORT);
				serverThread = new Thread(() -> Accept());
				serverThread.setName("serverThread");
				serverThread.start();
				serverName = "Server";

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
				serverSocket = null;
				
				System.out.println("[SERVER] Server Stopped");
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("[SERVER] Cannot stop server. Server already stopped!");
		}
		OnServerStop();
	}

	public boolean isRunning() {
		if(serverSocket != null)
			return !serverSocket.isClosed();
		else
			return false;
	}

	private void Accept() {
		try {
			System.out.println("[SERVER] Waiting for client to connect...");
			Socket s = serverSocket.accept();
			Connection<T> newconn = new Connection<T>(Connection.Group.server, s, qMessageIn);
			if(OnClientConnect(newconn)) {
				connections.addLast(newconn);
				connections.getLast().ConnectToClient(idCounter++);
				System.out.println("[NEW CLIENT] Client connected: " + newconn.getSocket().toString());
				System.out.println("[SERVER] No. of clients: " + clientNumber());
			}
			Accept();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void MessageClient(Connection<T> client, OwnedMessage<T> msg) {
		if(client != null && client.isConnected()) {
			client.Send(msg);
		} else {
			client.Disconnect();
			connections.remove(client);
		}
	}

	public void MessageAllClients(OwnedMessage<T> msg, Connection<T> ignoreClient) {
		for (Connection<T> c : connections) {
			if(c != ignoreClient)
				if(c != null && c.isConnected())
					c.Send(msg);
				else
					connections.remove(c);
		}
	}

	public Connection<T> getClient(int id) {
		for (Connection<T> connection : connections)
			if(connection.getId() == id)
				return connection;

		return null;
	}

	public void removeClient(int id) {
		for (Connection<T> connection : connections) {
			if (connection.getId() == id)
				connections.remove(connection);
		}
	}

	public Deque<Connection<T>> getClients() {
		return connections;
	}

	public TsQueue<T> Incoming() {
		return qMessageIn;
	}

	public Deque<Connection<T>> getConnections() {
		return connections;
	}

	public void Update(boolean bWait) {
		if(bWait) qMessageIn.Wait();

		while(!qMessageIn.empty()) {
			OwnedMessage<T> msg = qMessageIn.popFront();
			OnMessage(msg.getRemote(), msg.getMsg());
		}
	}

	// Override
	public boolean OnClientConnect(Connection<T> client) { return false; }

	public void OnMessage(Connection<T> client, Message<T> msg) {}

	public void OnClientDisconnect(Connection<T> client) {}

	public void OnServerStop() {}

	public int clientNumber() {
		return connections.size();
	}
}