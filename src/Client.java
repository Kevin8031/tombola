import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class Client extends Network {

	private Socket socket;
	private Thread clientThread;
	private String name;
	private MulticastSocket multicastSocket;
	private Thread multicastThread;
	private boolean searchGame;

	Client() {
		group = Group.player;
	}
	
	Client(Socket socket, int id) {
		group = Group.player;
		super.id = id;
		this.socket = socket;
	}

	public void DisconnectFromServer() {
		if(group == Group.player) {
			if(socket != null) {
				try {
					socket.close();
					clientThread.join(100);
					System.out.println("Disconnected");
				} catch (Exception e) {
					System.err.println(e);
				}
			}
			else
				System.out.println("Already disconnected!");
			
			StopLanSearch();
		}
	}

	public boolean ConnectToServer(String host, int port) {
		if(group == Group.player) {
			try {
				socket = new Socket();
				SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
				socket.connect(socketAddress);
				inStream = new Scanner(socket.getInputStream());
				outStream = new PrintStream(socket.getOutputStream());
				clientThread = new Thread(() -> {Init();});
				clientThread.start();
				System.out.println(socket.toString());
				
				return !socket.isClosed();
			} catch (IOException e) {
				System.err.println(e);
				return false;
			}
		} else {
			System.out.println("Only players can connect to servers.");
			return false;
		}
	}

	public void Init() {
		//Message msg = new Message(Net.SetName, "player");

		Read();
	}

	public void StartLanSearch() {
		multicastThread = new Thread(() -> LanSearch());
		multicastThread.setName("LanSearch");
		multicastThread.start();
	}

	private void LanSearch() {
		byte[] byt;
		searchGame = true;
		try {
			multicastSocket = new MulticastSocket(MULTICAST_PORT);
			InetAddress inet = InetAddress.getByName(MULTICAST_INET);
			multicastSocket.joinGroup(inet);
			System.out.println("Searching for a game.");
			while (searchGame) {
				// receive packet
				byt = new byte[256];
				DatagramPacket recv = new DatagramPacket(byt, byt.length);
				multicastSocket.receive(recv);

				// get packet body
				Message msg = Message.getHeadAndBody(new String(byt));
				System.out.println("Multicast message: " + msg);
				if(MessageType.valueOf(msg.getHead()).equals(MessageType.LAN_SERVER_DISCOVEY)) {
					msg.setBody(msg.getBody().trim());
					msg.Add(recv.getAddress().getHostAddress());
					Giocatore.ReadFromServer(msg);
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

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		super.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}