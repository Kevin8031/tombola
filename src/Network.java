import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Network {
	enum Group {
		player,
		host;

		public String toString() {
			return name();
		}
	}

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
		buf.put(inStream.nextLine().getBytes());

		Message msg = Message.getHeadAndBody(new String(buf.array()));
		msg.setBody(msg.getBody().trim());

		if(group == Group.host)
			Master.ReadFromClient(id, msg);
		else
			Giocatore.ReadFromClient(id, msg);

		buf.clear();
		Read();
	}

	public void SendNumber(int num) {
		outStream.println(MessageType.NewNumber.toString() + " " + num);
		System.out.println("[" + id + " - " + group + "] Sent: " + num);
	}

	public void Send(String s) {
		outStream.println(s);
		System.out.println("[" + id + " - " + group + "] Sent: " + s);
	}

	public void Send(Message msg) {
		outStream.println(msg.toString());
		System.out.println("[" + id + " - " + group + "] Sent: " + msg.toString());
	}

	
	
}