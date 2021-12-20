import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class Network {
    enum Group {
        player,
        host
    }

    Group group;
    Socket socket;
    private Thread thread;
    private Scanner inStream;
	private PrintStream outStream;
    private int id;
	private ByteBuffer buf;

    Network(Group group, int id, ByteBuffer buf) {
        this.group = group;
        this.id = id;
        this.buf = buf;
    }

    public boolean ConectToServer(String host, int port) {
        if(group == Group.player) {
            try {
                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
                socket.connect(socketAddress);
                inStream = new Scanner(this.socket.getInputStream());
			    outStream = new PrintStream(this.socket.getOutputStream());
                thread = new Thread(() -> {Init();});
                thread.start();
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

    }

    public void Read() {
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

    public void DisconnectFromServer() {
        if(group == Group.player) {
            if(socket != null) {
                try {
                    socket.close();
                    thread.join(100);
                    System.out.println("Disconnesso");
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            else
                System.out.println("Already disconnected!");
        }
        
    }
}
