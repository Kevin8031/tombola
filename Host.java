import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Host extends Network {

	Network.Group group;
	private ServerSocket serverSocket;
	private Thread serverThread;
	private Thread readThread;
	private ArrayList<Player> client;

	private static Map<Integer, ByteBuffer> map;
	private int uid;

	Host() {
		group = Network.Group.host;

		readThread = new Thread(() -> Read());
		client = new ArrayList<Player>();
		map = new HashMap<Integer, ByteBuffer>();
	}
	
	public boolean StartServer() {
		if(group == Group.host) {
			if(serverSocket == null) {
				try {
					serverSocket = new ServerSocket(60001);
					serverThread = new Thread(() -> Accept());
					System.out.println("Server started!");
					serverThread.start();
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
					for (Player i : client) {
						if(i != null)
							i.DisconnectFromServer();
					}

					serverThread.join(100);
					readThread.join(100);
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
				Player p = new Player(s, uid++);
				
				p.inStream = new Scanner(s.getInputStream());
				p.outStream = new PrintStream(s.getOutputStream());

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

	/*private void OpenToLan() {
		buf = new byte[256];
		try {
			multicastSocket = new MulticastSocket(4321);

			NetworkInterface nic = NetworkInterface.getByName("enp3s0");
			InetAddress inet = InetAddress.getByName("230.0.0.0");
			multicastSocket.joinGroup(inet);

			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				multicastSocket.receive(packet);
				String recived = new String(buf);
				System.out.println("Recived: " + recived);
			}

			// DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			// datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			// datagramChannel.bind(new InetSocketAddress(4321));
			// datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);

			// InetAddress inetAddress = InetAddress.getByName("230.0.0.0");

			// MembershipKey membershipKey = datagramChannel.join(inetAddress, nic);
			// System.out.println("Server opened to lan - port(4321)");
			// ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			// datagramChannel.read(byteBuffer);
			// byteBuffer.flip();
			// byte[] b = new byte[byteBuffer.limit()];
			// byteBuffer.get(b, 0, byteBuffer.limit());
			// membershipKey.drop();
			// System.out.println("Message: " + b);
		} catch (Exception e) {
			System.err.println(e);
		}
	}*/

	public ArrayList<Player> getClient() {
		return client;
	}

	public void setClient(ArrayList<Player> client) {
		this.client = client;
	}

	@Override
	public void SendNumber(int num) {
		for (Player player : client) {
			player.SendNumber(num);
		}
	}
}