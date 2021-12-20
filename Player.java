import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

public class Player extends Network {

	private Socket socket;
	private Thread clientThread;

	Player() {
		group = Group.player;
	}
	
	Player(Socket socket, int id) {
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
				inStream = new Scanner(this.socket.getInputStream());
				outStream = new PrintStream(this.socket.getOutputStream());
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
		Read();
	}

	//TODO: LAN SEARCH
	/*private void SearchGame() {
		String message = "Cerco Partita";
		ByteBuffer buf = ByteBuffer.wrap(message.getBytes());
		try {
			System.out.println("Searching lan game - port(4321)");
			DatagramChannel datagramChannel = DatagramChannel.open();
			NetworkInterface nic = NetworkInterface.getByIndex(1);
			InetSocketAddress inet = InetSocketAddress.createUnresolved("230.0.0.0", 4321);

			datagramChannel.bind(null);
			datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);
			datagramChannel.send(buf, inet);

			System.out.println("Multicast sent: " + message);
			System.out.println("Waiting response...");
			String response = WaitResponse(nic);
			System.out.println("Message recived: " + response);
		} catch (Exception e) {
			System.err.println(e);
		}
	}*/

	//TODO: LAN SEARCH
	/*private String WaitResponse(NetworkInterface nic) {
		try {
			System.out.println(NetworkInterface.getNetworkInterfaces());
			//NetworkInterface nicc = NetworkInterface.getByName("wlan1");
			String message = "MiConnetto";
			buf = message.getBytes();
			multicastSocket = new MulticastSocket();
			InetAddress group = InetAddress.getByName("230.0.0.0");
			multicastSocket.joinGroup(group);

			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4321);
			multicastSocket.send(packet);


			// DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			// datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			// datagramChannel.bind(new InetSocketAddress(4321));
			// datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);


			// MembershipKey membershipKey = datagramChannel.join(inetAddress, nic);
			// ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			// datagramChannel.read(byteBuffer);
			// byteBuffer.flip();
			// byte[] b = new byte[byteBuffer.limit()];
			// byteBuffer.get(b, 0, byteBuffer.limit());
			// membershipKey.drop();
			// return new String(b);
		} catch (Exception e) {
			System.err.println(e);
		}
		return "failed";   
	}*/

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}	
}
