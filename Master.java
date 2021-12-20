import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.nio.*;
import java.io.*;

public class Master extends Tabellone {
    // attributes (GUI)
	private JFrame frame;
	private JPanel panel1;
	private JPanel panel2;
	private JLabel numero;
	private int uid;
	private JButton[] caselle;
	private GridLayout griglia;
	private Tabellone tabellone;
	private static Map<Integer, ByteBuffer> map;

    // attributes (Networking)
	private ServerSocket serverSocket;
	private MulticastSocket multicastSocket;
	private ArrayList<ConnectionHandler> client;
	private Thread accThread;
	private byte[] buf;

	Master(JFrame parent) {
        frame = new JFrame("Tombola");
		panel1 = new JPanel();
		panel2 = new JPanel();
		numero = new JLabel("-Numero Uscito-");
		caselle = new JButton[90];
		griglia = new GridLayout(9, 10, 5, 5);
		numeriEstratti = new ArrayList<Integer>(90);
		tabellone = new Tabellone();

		client = new ArrayList<ConnectionHandler>();
		map = new HashMap<Integer, ByteBuffer>();
		uid = 0;
        
		frame.setSize(600, 350);
		frame.setLayout(new GridLayout(2,1));

		panel1.setBackground(Color.gray);
		panel1.add(new JButton("Genera Numero") {
			{
				addActionListener(e -> GeneraNumero());
			}
		});
		panel1.add(numero);
		panel1.setLayout(new GridLayout());

		panel2.setBackground(Color.darkGray);
		panel2.setLayout(griglia);

		GeneraTabella();

		frame.add(panel1);
		frame.add(panel2);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setJMenuBar(new JMenuBar() {
			{
				add(new JMenu("File") {
					{
						add(new JMenuItem("Exit") {
							{
								frame.dispose();
							}
						});
					}
				});

				add(new JMenu("Server") {
					{
						add(new JMenuItem("Start Server") {
							{
								addActionListener(e -> StartServer());
							}
						});
						add(new JMenuItem("Close Server") {
							{
								addActionListener(e -> StopServer());
							}
						});
					}
				});

				add(new JMenu("Partita") {
					{
						add(new JMenuItem("Reset") {
							{
								addActionListener(e -> ResetAll());
							}
						});
						add(new JMenuItem("Show Clients") {
							{
								addActionListener(e -> ListClients());
							}
						});
					}
				});
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				StopServer();
				parent.setVisible(true);
			}
		});

		frame.setVisible(true);
		StartServer();
	}

    // methods (GUI)
	private void GeneraTabella() {
		for(int i = 0; i < tabellone.getTabella().length; i++) {
			caselle[i] = new JButton(String.valueOf(i + 1));
			caselle[i].addActionListener(l -> CheatNumero(l));
			panel2.add(caselle[i]);
		}
	}

	public void GeneraNumero() {
		Random random = new Random();
		int num = 0;

		if(numeriEstratti.size() < 90) {
			do {
				num = random.nextInt(90) + 1;
			} while (numeriEstratti.indexOf(num) != -1);

			numeriEstratti.add(num);
			System.out.println("Random number [" + num + "]. " + numeriEstratti.size() + "/" + 90);

			numero.setText(String.valueOf(num));
			caselle[num - 1].setBackground(Color.BLACK);
			caselle[num - 1].setForeground(Color.WHITE);

			SendNumber(num);
		}
		else {
			new JOptionPane("Gioco Finito") {
				{
					showMessageDialog(this, "Gioco Finito");
				}
			};
		}
	}

	private void StartServer() {
		if(serverSocket == null) {
			try {
				serverSocket = new ServerSocket(60001);
				accThread = new Thread(() -> Accept());
				System.out.println("Server started!");
				accThread.start();
				// new Thread(() -> ReadFromClient()).start();
				//OpenToLan();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("Server already started.");
		}
	}

	private void StopServer() {
		if(serverSocket != null) {
			try {
				for (ConnectionHandler i : client) {
					if(i != null)
						i.close();
				}

				accThread.join(100);
				serverSocket.close();
				System.out.println("Server Stopped");
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		else {
			System.out.println("Cannot stop server. Server already stopped!");
		}
	}

	private void SendNumber(int number) {
		for (ConnectionHandler c : client)
			c.SendNumber(number);
	}


	private void Accept() {
		try {
			System.out.println("Waiting for client to connect...");
			map.put(uid, ByteBuffer.allocate(1024));
			ConnectionHandler ch = new ConnectionHandler(serverSocket.accept(), uid, map.get(uid));
			uid++;
			client.add(ch);
			// new Thread(() -> client.get(0)).start();
			System.out.println("Client connected: " + client.get(0).getSocket().toString());
			System.out.println("No. of clients: " + client.size());
			Accept();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private void ListClients() {
		JDialog dialog = new JDialog(frame, "Clients");
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		JList<String> jList = new JList<String>(dlm);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setSize(150, 150);

		dialog.add(jList);

		dialog.setVisible(true);

		for (ConnectionHandler i : client) {
			dlm.add(0, i.getSocket().getRemoteSocketAddress().toString());
		}
	}

	private void OpenToLan() {
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
	}

	private void CheatNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();

		int num = Integer.valueOf(btn.getText());

		numeriEstratti.add(num);
		System.out.println("Selected number [" + num + "]. " + numeriEstratti.size() + "/" + 90);

		numero.setText(String.valueOf(num));
		caselle[num - 1].setBackground(Color.BLACK);
		caselle[num - 1].setForeground(Color.WHITE);
		SendNumber(num);
	}

	public static void ReadFromClient(int id, String msg) {
		// map.forEach((k, v) -> {
		//     System.out.println("[" + v + "] Says: " + new String(v.array()));
		// });

		System.out.println("[" + id + "] Says: " + msg);
	}

	private void ResetAll() {
		for (JButton btn : caselle) {
			panel2.remove(btn);
			btn = null;
		}		

		Reset();
		GeneraTabella();
		frame.validate();
		frame.repaint();
		System.out.println("Game resetted!");
	}
}