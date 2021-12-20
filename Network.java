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
	}

	// client

	public void Read() {
		buf.put(inStream.nextLine().getBytes());
		String msg = new String(buf.array());

		if(group == Group.host)
			Master.ReadFromClient(id, msg);

		buf.clear();
		Read();
	}

	public void SendNumber(int num) {
		outStream.println(num);
		System.out.println("[" + id + " - " + group + "] Sent: " + num);
	}

	public void Send(String s) {
		outStream.println(s);
		System.out.println("[" + id + " - " + group + "] Sent: " + s);
	}

	

}
