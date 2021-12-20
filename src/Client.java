package src;
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
	private Thread sendThread;
	private Thread reciveThread;

	Client() {
		group = Group.player;
		try {
			multicastSocket = new MulticastSocket(4321);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendThread = new Thread(() -> SearchGame());
		sendThread.start();
		reciveThread = new Thread(() -> ReciveGame());
		reciveThread.start();
	}
	
	Client(Socket socket, int id) {
		group = Group.player;
		super.id = id;
		this.socket = socket;
		sendThread = new Thread(() -> SearchGame());
		sendThread.start();
	}

	public void DisconnectFromServer() {
		if(group == Group.player) {
			if(socket != null) {
				try {
					socket.close();
					clientThread.join(100);
					System.out.println("Disconnesso");
				} catch (Exception e) {
					System.err.println(e);
				}
			}
			else
				System.out.println("Already disconnected!");
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

	//TODO: LAN SEARCH
	private void SearchGame() {
		String message = "Cerco Partita";
		byte[] byt;
		
		try {
			InetAddress inet = InetAddress.getByName("228.5.6.7");
			multicastSocket.joinGroup(inet);
			while (true) {
				DatagramPacket send = new DatagramPacket(message.getBytes(), message.length(), inet, 4321);
				multicastSocket.send(send);
				System.out.println("Multicast: " + message);

				Thread.sleep(5000);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void ReciveGame() {
		byte[] byt;
		try {
			MulticastSocket multicastSocket2 = new MulticastSocket(4322);
			InetAddress inet = InetAddress.getByName("228.5.6.7");
			multicastSocket2.joinGroup(inet);
			while (true) {
				byt = new byte[256];
				DatagramPacket recv = new DatagramPacket(byt, byt.length);
				multicastSocket2.receive(recv);
				String msg = new String(byt);
				System.out.println("Multicast answer: " + msg);
				System.out.println(multicastSocket2.getRemoteSocketAddress().toString());
			}
		} catch (IOException e) {
			System.err.println(e);
		}
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