package net;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;

import gui.Giocatore;

public class Client<T> {
	private Thread multicastThread;
	private MulticastSocket multicastSocket;
	private boolean searchGame;

	private Connection<T> connection;
	private Queue<T> qMessageIn;

	public Client() {
		qMessageIn = new Queue<T>();
	}

	public boolean Connect(String host, int port) {
		System.out.println("Attempting connection to: " + host + ":" + port);
			try {
				Socket socket = new Socket();
				SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
				connection = new Connection<T>(Connection.Group.client, socket, qMessageIn);
				connection.ConnectToServer(socketAddress);
				System.out.println("Successfully connected to: " + socket.toString());
				return true;
			} catch (IOException e) {
				System.err.println(e);
				return false;
			}
	}

	public void Disconnect() {
		if(IsConnected())
		connection.Disconnect();
	}
	
	public boolean IsConnected() {
		if(connection != null)
			return connection.isConnected();
		else
			return false;
	}

	public void Send(Message<T> msg) {
		if(IsConnected())
			connection.Send(msg);
	}

	public Queue<T> Incoming() {
		return qMessageIn;
	}

	public void StartLanSearch() {
		multicastThread = new Thread(() -> LanSearch());
		multicastThread.setName("LanSearch");
		multicastThread.start();
	}

	private void LanSearch() {
		byte[] data;
		searchGame = true;
		try {
			multicastSocket = new MulticastSocket(Common.MULTICAST_PORT);
			InetAddress inet = InetAddress.getByName(Common.MULTICAST_INET);
			multicastSocket.joinGroup(inet);
			System.out.println("Searching for a game.");
			while (searchGame) {
				// receive packet
				data = new byte[6400];
				DatagramPacket recv = new DatagramPacket(data, data.length);
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
				multicastSocket.receive(recv);
				ObjectInputStream ois = new ObjectInputStream(bais);
				Message<T> msg = null;
				try {
					msg = (Message<T>)ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				// get packet body
				System.out.println("Multicast message: " + msg);
				if(msg.getHeadId() == MessageType.LAN_SERVER_DISCOVEY) {
					msg.Add(recv.getAddress().getHostAddress());
					qMessageIn.pushFront(msg);
					Giocatore.Notify();
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void StopLanSearch() {
		if(multicastSocket != null) {
			searchGame = false;
			try {
				multicastThread.join(100);
				multicastSocket.close();
				System.out.println("Stopped searching for a game.");
				multicastSocket = null;
			} catch (Exception e) {
				System.err.println(e);
			}
		} else
			System.out.println("Lan search already stopped.");
	}

	public String getName() {
		return connection.name;
	}

	public void setName(String name) {
		connection.name = name;
	}
}