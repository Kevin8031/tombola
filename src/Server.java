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
	private MulticastSocket multicastSend;
	private MulticastSocket multicastRecive;
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
					serverThread.start();
					lanThread = new Thread(() -> OpenToLan());
					lanThread.start();
					System.out.println("Server started!");
					return true;
					// new Thread(() -> ReadFromClient()).start();
					//OpenToLan();
				} catch (IOException e) {
					System.err.println(e);
					return false;
				}
			}
			else {
				System.out.println("Server already started.");
				return true;
			}
		} else {
			System.out.println("Only hosts can connect to servers.");
			return false;
		}
	}

	public void StopServer() {
		if(group == Group.host) {
			if(serverSocket != null) {
				try {
					for (Client i : client) {
						if(i != null)
							i.DisconnectFromServer();
					}

					serverThread.join(100);
					lanThread.join(100);
					//readThread.join(100);
					serverSocket.close();
					System.out.println("Server Stopped");
				} catch (Exception e) {
					System.err.println(e);
				}
			}
			else {
				System.out.println("Cannot stop server. Server already stopped!");
			}
		} else {
			System.out.println("Only hosts can stop servers.");
		}
	}

	private void Accept() {
		if(group == Group.host) {
			try {
				System.out.println("Waiting for client to connect...");
				map.put(uid, ByteBuffer.allocate(1024));
				//ConnectionHandler ch = new ConnectionHandler(serverSocket.accept(), uid, map.get(uid));
				Socket s = serverSocket.accept();
				Client p = new Client(s, uid++);
				
				p.inStream = new Scanner(s.getInputStream());
				p.outStream = new PrintStream(s.getOutputStream());
				p.setName(("player" + uid));

				new Thread(() -> p.Read()).start();;
				client.add(p);
				// new Thread(() -> client.get(0)).start();
				System.out.println("Client connected: " + client.get(0).getSocket().toString());
				System.out.println("No. of clients: " + client.size());
				Accept();
			} catch (IOException e) {
				System.err.println(e);
			}
		} else {
			System.out.println("Only hosts can accept clients.");
		}
	}

	private void OpenToLan() {
		byte[] byt = new byte[256];
		try {
			multicastSend = new MulticastSocket(4321);
			multicastRecive = new MulticastSocket(4321);
			
			//NetworkInterface nic = NetworkInterface.getByName("enp3s0");
			InetAddress inetRecive = InetAddress.getByName("228.5.6.7");
			InetAddress inetSend = InetAddress.getByName("228.5.6.8");
			multicastSend.joinGroup(inetRecive);
			multicastRecive.joinGroup(inetSend);
			
			System.out.println("Server opened to lan.");
			while (openToLan) {
				DatagramPacket recv = new DatagramPacket(byt, byt.length);
				multicastSend.receive(recv);
				String msg = new String(byt);
				msg = msg.trim();

				if(msg.equals("Cerco Partita")) {
					System.out.println("Mutlicast recived: " + msg);
					String message = multicastSend.getLocalSocketAddress().toString();
					message = serverSocket.getLocalSocketAddress().toString();
					DatagramPacket send = new DatagramPacket(message.getBytes(), message.length(), inetRecive, 4322);
					multicastRecive.send(send);
					System.out.println("Sending multicast:" + message);
				}

				byt = new byte[256];
			}
			
		} catch (Exception e) {
			System.err.println(e);
			System.out.println("Cannot open server to lan.");
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