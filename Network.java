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

		String s = new String(buf.array());
		String msg = getHead(s);

		if(group == Group.host)
			Master.ReadFromClient(id, msg);

		buf.clear();
		Read();
	}

	public void SendNumber(int num) {
		outStream.println(Net.NewNumber.toString() + " " + num);
		System.out.println("[" + id + " - " + group + "] Sent: " + num);
	}

	public void Send(String s) {
		outStream.println(s);
		System.out.println("[" + id + " - " + group + "] Sent: " + s);
	}

	
	public String getHead(String s) {
		int i = 0;
		while(i < s.length() - 1) {
			if(s.charAt(i++) == ' ')
				break;
		}

		String head = s.substring(0, i - 1);
		String msg = s.substring(i, s.length());
		System.out.println("Head: " + head + " - body: " + msg);
		return head;
	}
}