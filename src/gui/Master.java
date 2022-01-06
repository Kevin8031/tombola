package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import game.Tabellone;
import net.Connection;
import net.Message;
import net.MessageType;
import net.Server;
public class Master extends Tabellone {
	// attributes (GUI)
	private JFrame frame;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel topPanel;
	private JPanel buttonPanel;
	private JPanel panel1;
	private JLabel numero;
	private GridLayout griglia;
	private Tabellone tabellone;
	private JButton[] caselle;
	private Image image;
	private static Thread readThread;
	private boolean retry = true;
	
	// attributes (Network)
	private Server<MessageType> server;

	// constructor
	public Master(JFrame parent) {
		frame = new JFrame("Tombola");
		centerPanel = new JPanel();
		leftPanel = new JPanel();
		topPanel = new JPanel();
		buttonPanel = new JPanel();
		panel1 = new JPanel();
		numero = new JLabel("-Numero Uscito-");
		griglia = new GridLayout(9, 10, 5, 5);
		tabellone = new Tabellone();
		caselle = new JButton[90];
		numeriEstratti = new ArrayList<Integer>(90);
		readThread = new Thread(() -> ReadFromClient());
		readThread.setName("ReadThread");
		server = new Server<MessageType>();
		
		// setters (frame)
		frame.setSize(600, 350);
		frame.setLayout(new BorderLayout());
		image = Toolkit.getDefaultToolkit().getImage("C://Users//arman//Desktop//GITHUB//Repos//tombola//src//icon.png");
		frame.setIconImage(image);
		
		// setters (panel1)
		panel1.setBackground(Color.gray);
		panel1.add(new JButton("Genera Numero") {
			{
				addActionListener(e -> GeneraNumero());
			}
		});
		panel1.add(numero);
		panel1.setLayout(new GridLayout());
		
		buttonPanel.setBackground(Color.darkGray);
		buttonPanel.setLayout(griglia);
		
		GeneraTabella();

		// setters (-Panel)
		centerPanel.setSize(new Dimension(100, 100));
		centerPanel.add(buttonPanel);
		centerPanel.setBackground(Color.BLUE);
		centerPanel.setLayout(new GridLayout());

		leftPanel.setSize(110, 100);

		topPanel.setSize(100, 100);
		topPanel.add(new JButton("Genera Numero") {
			{
				addActionListener(e -> GeneraNumero());
			}
		});

		frame.add(leftPanel, BorderLayout.WEST);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(topPanel, BorderLayout.NORTH);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// MenuBar
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
								addActionListener(e -> server.Start(true));
							}
						});
						add(new JMenuItem("Start Open To Lan") {
							{
								addActionListener(e -> server.StartOpenToLan());
							}
						});
						add(new JMenuItem("Stop Open To Lan") {
							{
								addActionListener(e -> server.StopOpenToLan());
							}
						});
						add(new JMenuItem("Close Server") {
							{
								addActionListener(e -> server.Stop());
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
				server.Stop();
				parent.setVisible(true);
			}
		});

		frame.setVisible(true);
		server.Start(true);
		readThread.start();
	}

	// methods (GUI)
	private void GeneraTabella() {
		for(int i = 0; i < tabellone.getTabella().length; i++) {
			caselle[i] = new JButton(String.valueOf(i + 1));
			caselle[i].addActionListener(l -> CheatNumero(l));
			buttonPanel.add(caselle[i]);
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

			Message<MessageType> msg = new Message<MessageType>(MessageType.NewNumber, num);
			server.MessageAllClients(msg, null);
		}
		else {
			new JOptionPane("Gioco Finito") {
				{
					showMessageDialog(this, "Gioco Finito");
				}
			};
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

		Message<MessageType> msg = new Message<MessageType>(MessageType.NewNumber, num);
		server.MessageAllClients(msg, null);
	}

	public void ReadFromClient() {
		while(retry) {
			if(server.Incoming().count() > 0) {
				// System.out.println("[" + id + " \"" + server.getClient(id).getName() + "\"" + "] Says: " + msg.toString());
				Message<MessageType> msg = server.Incoming().popBack();
				int id = msg.getId();
				switch (msg.getHeadId()) {
					case SetName:
						String s = new String();
						s = msg.Get(s);
						Message<MessageType> msg1 = new Message<MessageType>(MessageType.SetName);
						if(!s.equals("null")) {
							server.getClient(id).setName(s);
							System.out.println("Name set for: " + id + " \"" + server.getClient(id).getName() + "\"");
							msg.Add("true");
							server.getClient(id).Send(msg1);
						} else
						System.out.println("No name set for: " + id + " \"" + server.getClient(id).getName() + "\". Setting a default name");
							msg1.Add(server.getClient(id).getName());
							server.getClient(id).Send(msg1);
						break;

					// case Disconnect:
					// 	server.getClient(id).DisconnectFromServer();
					// 	server.getClients().remove(id, server.getClient(id));
					// 	break;
				
					case GetTabella:
						ShowClientTable(msg, id);
						break;

					default:
						System.out.println("Invalid message");
						break;
				}
			}
			try {
				synchronized (Master.readThread) {
					readThread.wait();
				}
				ReadFromClient();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
	}

	private void ResetAll() {
		for (JButton btn : caselle) {
			buttonPanel.remove(btn);
			btn = null;
		}		

		Reset();
		GeneraTabella();
		frame.validate();
		frame.repaint();
		System.out.println("Game resetted!");
	}

	private void ListClients() {
		JDialog dialog = new JDialog(frame, "Clients");
		DefaultListModel<String> dlm = new DefaultListModel<String>();
		JList<String> list = new JList<String>(dlm);
		JScrollPane scroll = new JScrollPane(list);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setSize(400, 150);

		dialog.add(scroll);

		dialog.setVisible(true);

		for (Connection<MessageType> conn : server.getConnections()) {
			dlm.add(conn.getId(), conn.getName());
		}

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					server.getClient(list.getSelectedIndex()).Send(new Message<MessageType>(MessageType.GetTabella));
				}
			}
		});

	}

	private void ShowClientTable(Message<MessageType> msg, int id) {
		System.out.println("Showing table of " + id);
		JDialog dialog = new JDialog();

		ArrayList<Cartella> cartelle = null;
		cartelle = msg.Get(cartelle);

		for (int i = cartelle.size() - 1; i >= 0; i--) {
			dialog.add(cartelle.get(i));
		}
		dialog.setLayout(new FlowLayout());
		dialog.setSize(400, 400);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public static void Notify() {
		synchronized (Master.readThread) {
			readThread.notify();
		}
	}
}