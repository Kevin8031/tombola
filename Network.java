import java.io.IOException;
import java.net.*;

public class Network {
    private Thread thread;

    public boolean ConectToServer(String host, int port) {
        try {
			Socket socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
			socket.connect(socketAddress);
			thread = new Thread(() -> {ReadNumber();});
			thread.start();
			System.out.println(socket.toString());
			
			return !socket.isClosed();
		} catch (IOException e) {
			System.err.println(e);
			return false;
		}
    }

    public int ReadNumber() {
        return 0;
        // ReadNumber();
    }
}
