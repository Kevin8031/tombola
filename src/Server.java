import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server extends Network {

	private Network.Group group;
	private ServerSocket serverSocket;
	private MulticastSocket multicastRecive;
	private MulticastSocket multicastSend;
	private Thread serverThread;
	private Thread lanThread;
	private ArrayList<Client> client;
	private boolean openToLan;
	private String serverName;

	private static Map<Integer, ByteBuffer> map;
	private int uid;

	Server() {
		group = Network.Group.host;

		//readThread = new Thread(() -> Read());
		//readThread.start();
		client = new ArrayList<Client>();
		map = new HashMap<Integer, ByteBuffer>();
		openToLan = true;
	}
	
	public boolean StartServer() {
		if(group == Group.host) {
			if(serverSocket == null) {
				try {
					serverSocket = new ServerSocket(60001);
					serverThread = new Thread(() -> Accept());
					serverThread.setName("serverThread");
					serverThread.start();
					lanThread = new Thread(() -> OpenToLan());
					lanThread.setName("lanThread");
					lanThread.start();
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
					openToLan = false;
					for (Client i : client) {
						if(i != null) {
							i.DisconnectFromServer();
							i.inStream.close();
							i.outStream.close();
						}
					}

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

			if(multicastRecive != null) {
				try {
					lanThread.join(1);
					multicastRecive.close();

					System.out.println("[SERVER] Lan discovery (send) stopped. ");
				} catch (Exception e) {
					System.err.println(e);
				}
			} else
				System.out.println("[SERVER] Lan discovery already stopped. ");

			if(multicastSend != null) {
				try {
					multicastSend.close();

					System.out.println("[SERVER] Lan discovery (recive) stopped. ");
				} catch (Exception e) {
					System.err.println(e);
				}
			} else
				System.out.println("[SERVER] Lan discovery already stopped. ");

		} else {
			System.out.println("[SERVER] Only hosts can stop servers.");
		}
	}

	private void Accept() {
		if(group == Group.host) {
			try {
				System.out.println("[SERVER] Waiting for client to connect...");
				map.put(uid, ByteBuffer.allocate(1024));
				Socket s = serverSocket.accept();
				Client p = new Client(s, uid++);
				
				p.inStream = new Scanner(s.getInputStream());
				p.outStream = new PrintStream(s.getOutputStream());
				p.setName(("player" + uid));

				new Thread(() -> p.Read()).start();;
				client.add(p);
				System.out.println("[NEW CLIENT] Client connected: " + client.get(0).getSocket().toString());
				System.out.println("[SERVER] No. of clients: " + client.size());
				Accept();
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			System.out.println("[SERVER] Only hosts can accept clients.");
		}
	}

	private void OpenToLan() {
		try {
			multicastSend = new MulticastSocket(4321);
			
			InetAddress inetSend = InetAddress.getByName("228.5.6.7");
			multicastSend.joinGroup(inetSend);
			
			System.out.println("[SERVER] Server opened to lan.");
			String msg = new String("LAN_SERVER_DISCOVEY_" + serverName);

			while (openToLan) {
				DatagramPacket send = new DatagramPacket(msg.getBytes(), msg.length(), inetSend, 4322);
				multicastSend.send(send);
				msg = msg.trim();
				System.out.println("[LAN SEARCH] Sent: " + msg);
				Thread.sleep(5000);
			}
			
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("[SERVER] Cannot open server to lan.");
		}
	}

	public ArrayList<Client> getClients() {
		return client;
	}

	public Client getClient(int id) {
		for (Client player : client) {
			if(player.getId() == id)
				return player;
		}

		return null;
	}

	public void setClient(ArrayList<Client> client) {
		this.client = client;
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
	public void SendNumber(int num) {
		for (Client player : client) {
			player.SendNumber(num);
		}
	}
}