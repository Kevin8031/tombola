import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ConnectionHandler {
	private Socket socket;
	private Scanner inStream;
	private PrintStream outStream;
	private int id;
	private ByteBuffer buf;


	ConnectionHandler(Socket socket, int id, ByteBuffer buf) {
		this.socket = socket;
		this.id = id;
		this.buf = buf;

		try {
			inStream = new Scanner(this.socket.getInputStream());
			outStream = new PrintStream(this.socket.getOutputStream());
		} catch (IOException e) {
			System.err.println(e);
		}

		new Thread(() -> Read()).start(); 
	}

	public void Read() {
		//System.out.println("[" + id + "]" + socket.getRemoteSocketAddress() + inStream.nextLine());
		// return inStream.nextLine();
		buf.put(inStream.nextLine().getBytes());
		String msg = new String(buf.array());
		Master.ReadFromClient(id, msg);
		buf.clear();
		Read();
	}

	public void SendNumber(int num) {
		outStream.println(num);
	}

	public void Send(String s) {
		outStream.println(s);
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getSocket() {
		return socket;
	}
}
