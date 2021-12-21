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
	private MulticastSocket multicastSend;
	private MulticastSocket multicastRecive;
	private Thread reciveThread;
	private boolean searchGame;

	Client() {
		group = Group.player;
		
		// sendThread = new Thread(() -> SearchGame());
		// sendThread.start();
		// reciveThread = new Thread(() -> ReciveGame());
		// reciveThread.start();
	}
	
	Client(Socket socket, int id) {
		group = Group.player;
		super.id = id;
		this.socket = socket;
		reciveThread = new Thread(() -> SearchGame());
		reciveThread.start();
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

	// private void SearchGame() {
	// 	String message = "Cerco Partita";		
	// 	byte[] byt;
	// 	searchGame = true;
	// 	try {
	// 		multicastSend = new MulticastSocket(4321);
	// 		InetAddress inetSend = InetAddress.getByName("228.5.6.7");
	// 		multicastSend.joinGroup(inetSend);
	// 		while (searchGame) {
	// 			DatagramPacket send = new DatagramPacket(message.getBytes(), message.length(), inetSend, 4321);
	// 			multicastSend.send(send);
	// 			System.out.println("Multicast: " + message);

	// 			byt = new byte[256];
	// 			DatagramPacket recv = new DatagramPacket(byt, byt.length);
	// 			multicastSend.receive(recv);
	// 			String msg = new String(byt);
	// 			System.out.println("Multicast answer: " + msg);
	// 			// System.out.println(multicastSend.getInetAddress().toString());

	// 			Thread.sleep(5000);
	// 		}
	// 	} catch (Exception e) {
	// 		System.err.println(e);
	// 	}
	// }

	private void SearchGame() {
		byte[] byt;
		searchGame = true;
		try {
			multicastRecive = new MulticastSocket(4321);
			InetAddress inetRecive = InetAddress.getByName("228.5.6.7");
			multicastRecive.joinGroup(inetRecive);
			while (searchGame) {
				byt = new byte[256];
				DatagramPacket recv = new DatagramPacket(byt, byt.length);
				multicastRecive.receive(recv);
				String msg = new String(byt);
				System.out.println("Multicast message: " + msg);
				System.out.println(recv.getAddress().getHostAddress());
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