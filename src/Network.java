import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Network {
	enum Group {
		player,
		host;

		public String toString() {
			return name();
		}
	}

	final protected int MULTICAST_PORT = 8888;
	final protected String MULTICAST_INET = "237.5.6.7";
	final protected int SERVER_PORT = 4321;
	
	// client
	protected int id;

	// both
	protected Group group;
	protected Scanner inStream;
	protected PrintStream outStream;
	protected ByteBuffer buf;

	Network() {
		// this.group = group;
		// this.id = id;
		// this.buf = buf;
		// client = new ArrayList<Socket>();
		// map = new HashMap<Integer, ByteBuffer>();
		buf = ByteBuffer.allocate(1024);
	}

	// client

	public void Read() {
		try {
			buf.put(inStream.nextLine().getBytes());
			Message msg = Message.getHeadAndBody(new String(buf.array()));
			msg.setBody(msg.getBody().trim());

			if(group == Group.player)
				Master.ReadFromClient(id, msg);
			else
				Giocatore.ReadFromServer(msg);

			buf.clear();
			Read();
		} catch (NoSuchElementException e) {
			System.err.println(e);
			System.out.println("[ERROR] Server closed.");
		}
	}

	public void Send(String s) {
		if(outStream != null) {
			outStream.println(s);
			System.out.println("[" + id + " - " + group + "] Sent: " + s);
		} else
			System.out.println("[ERROR] Cannot send message \"" + s + "\". Not connected to anyone.");
	}

	public void Send(Message msg) {
		if(outStream != null) {
			outStream.println(msg.toString());
			System.out.println("[" + id + " - " + group + "] Sent: " + msg.toString());
		} else
			System.out.println("[ERROR] Cannot send message \"" + msg.toString() + "\". Not connected to anyone.");
	}

	
	
}