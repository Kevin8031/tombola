package net;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server extends Network {

	private Network.Group group;
	private ServerSocket serverSocket;
	private MulticastSocket multicastSocket;
	private Thread serverThread;
	private Thread lanThread;
	private static Map<Integer, Client> client;
	private boolean openToLan;
	private String serverName;

	private static Map<Integer, ByteBuffer> map;
	private int uid;

	public Server() {
		group = Network.Group.host;

		//readThread = new Thread(() -> Read());
		//readThread.start();
		client = new HashMap<Integer, Client>();
		map = new HashMap<Integer, ByteBuffer>();
		openToLan = true;
	}
	
	public boolean StartServer() {
		if(group == Group.host) {
			if(serverSocket == null) {
				try {
					serverSocket = new ServerSocket(SERVER_PORT);
					serverThread = new Thread(() -> Accept());
					serverThread.setName("serverThread");
					serverThread.start();

					System.out.println("[SERVER] Server started!");
					serverName = new String("Tombola");
					System.out.println("[SEVRER] Name: " + serverName);
					return true;
				} catch (IOException e) {
					System.err.println(e);
					return false;
				}
			}
			else {
				System.out.println("[SERVER] Server already started.");
				return true;
			}
		} else {
			System.out.println("[SERVER] Only hosts can connect to servers.");
			return false;
		}
	}

	public void StopServer() {
		if(group == Group.host) {
			if(serverSocket != null) {
				try {
					client.forEach((k, v) -> {
						if(v != null) {
							v.DisconnectFromServer();
							v.inStream.close();
							v.outStream.close();
						}
					});
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
		} else {
			System.out.println("[SERVER] Only hosts can stop servers.");
		}
	}

	public boolean isServerStarted() {
		return !serverSocket.isClosed();
	}

	private void Accept() {
		if(group == Group.host) {
			try {
				System.out.println("[SERVER] Waiting for client to connect...");
				map.put(uid, ByteBuffer.allocate(1024));
				Socket s = serverSocket.accept();
				Client p = new Client(s, uid);
				p.group = Group.host;
				
				p.inStream = new Scanner(s.getInputStream());
				p.outStream = new PrintStream(s.getOutputStream());
				p.setName(("player" + uid));

				new Thread(() -> p.Read()).start();
				client.put(uid, p);
				System.out.println("[NEW CLIENT] Client connected: " + client.get(uid++).getSocket().toString());
				System.out.println("[SERVER] No. of clients: " + client.size());
				Accept();
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			System.out.println("[SERVER] Only hosts can accept clients.");
		}
	}

	public void StartOpenToLan() {
		lanThread = new Thread(() -> OpenToLan());
		lanThread.setName("lanThread");
		lanThread.start();
		if(!isServerStarted()) {
			System.out.println("[LAN SEARCH] Server was not started. Starting...");
			StartServer();
		}
	}

	private void OpenToLan() {
		try {
			multicastSocket = new MulticastSocket();
			InetAddress inet = InetAddress.getByName(MULTICAST_INET);
			multicastSocket.joinGroup(inet);
			System.out.println("[SERVER] Server opened to lan.");
			Message msg = Message.getHeadAndBody(new String(MessageType.LAN_SERVER_DISCOVEY + " " + serverName + " " + serverSocket.getLocalPort()));

			while (openToLan) {
				DatagramPacket send = new DatagramPacket(msg.toString().getBytes(), msg.toString().length(), inet, MULTICAST_PORT);
				multicastSocket.send(send);
				System.out.println("[LAN SEARCH] Sent: " + msg);
				Thread.sleep(5000);
			}
			
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("[SERVER] Cannot open server to lan.");
		}
	}

	public void StopOpenToLan() {
		if(multicastSocket != null) {
			openToLan = false;
			try {
				lanThread.join(100);
				multicastSocket.close();
				multicastSocket = null;
				System.out.println("[SERVER] Not visible on lan");
			} catch (Exception e) {
				System.err.println(e);
			}
		} else
			System.out.println("[SERVER] Lan visibility already stopped");
	}

	public static void RemoveClient(int id) {
		client.forEach((k, v) -> {
			if(k.equals(id))
				client.remove(k, v);
		});
	}

	public Map<Integer, Client> getClients() {
		return client;
	}

	public Client getClient(int id) {
		Client c[] = new Client[1];

		client.forEach((k, v) -> {
			if(k.equals(id))
				c[0] = v;
		});

		return c[0];
	}

	public void setClient(Map<Integer, Client> client) {
		Server.client = client;
	}
	
	public boolean isOpenToLan() {
		return openToLan;
	}

	public void setOpenToLan(boolean openToLan) {
		this.openToLan = openToLan;

		if(openToLan)
			lanThread.start();
	}

	@Override
	public void Send(Message msg) {
		client.forEach((k, v) -> {
			v.Send(msg);
		});
	}
}