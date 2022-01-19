package client.gui;
import javax.swing.*;
import javax.swing.border.Border;

import client.game.Tabellone;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import net.Connection;
import net.Message;
import net.MessageType;
import net.OwnedMessage;
import net.Server;
public class Master extends Tabellone {
	// constants
	final private Dimension dim = new Dimension(35, 20);
	final private Color color = new Color(192, 202, 202);

	// attributes (GUI)
	private JFrame frame;
	private JPanel centerPanel;
	private JPanel topPanel;
	private JPanel buttonPanel;
	private JLabel numero;
	private GridLayout griglia;
	private Tabellone tabellone;
	private JButton[] caselle;
	private Image image;
	private static Thread readThread;
	private boolean retry = true;
	private Border blackline;
	
	// attributes (Network)
	private Server<MessageType> server;

	// constructor
	public Master(JFrame parent) {
		frame = new JFrame("Tombola");
		centerPanel = new JPanel();
		topPanel = new JPanel();
		buttonPanel = new JPanel();
		numero = new JLabel("-Numero Uscito-");
		griglia = new GridLayout(9, 10);
		tabellone = new Tabellone();
		caselle = new JButton[90];
		numeriEstratti = new ArrayList<Integer>(90);
		readThread = new Thread(() -> ReadFromClient());
		readThread.setName("ReadThread");
		server = new Server<MessageType>();
		blackline = BorderFactory.createLineBorder(Color.BLACK, 1);
		
		// setters (frame)
		frame.setSize(750, 500);
		frame.setLayout(new BorderLayout());
		image = Toolkit.getDefaultToolkit().getImage("res/icon.png");
		frame.setIconImage(image);
		
		buttonPanel.setBackground(Color.darkGray);
		buttonPanel.setLayout(griglia);
		
		GeneraTabella();

		// setters (-Panel)
		centerPanel.setBackground(Color.BLUE);
		centerPanel.setLayout(new GridLayout());
		centerPanel.add(buttonPanel);

		topPanel.setPreferredSize(new Dimension(frame.getX(), 60));
		topPanel.setLayout(new FlowLayout(4 /* CENTER */, 65, 5));
		topPanel.add(new JButton("Genera Numero") {
			{
				setBackground(Color.WHITE);
				setForeground(Color.BLACK);
				setPreferredSize(new Dimension(180, 50));
				setFont(new Font("Roboto", Font.BOLD, 17));
				setFocusable(false);
				setEnabled(true);
				addActionListener(e -> GeneraNumero());
				addMouseListener(new MouseAdapter() {
					Color c = null;
					public void mouseEntered(MouseEvent evt) {
						c = getBackground();
						setBackground(color);
					}
					
					public void mouseExited(MouseEvent evt) {
						setBackground(c);
					}
				});
			}
		});
		topPanel.add(new JButton("Mostra Giocatori") {
		 	{
				setBackground(Color.WHITE);
				setForeground(Color.BLACK);
				setFont(new Font("Roboto", Font.BOLD, 13));
				setPreferredSize(new Dimension(150, 30));
				setFocusable(false);
				setEnabled(true);
		 		addActionListener(e -> ListClients());
		 	}
		});
		
		frame.add(topPanel, BorderLayout.NORTH);
		frame.add(centerPanel, BorderLayout.CENTER);
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
								addActionListener(e -> server.Start());
							}
						});
						add(new JMenuItem("Start Open To Lan") {
							{
								// addActionListener(e -> server.StartOpenToLan());
							}
						});
						add(new JMenuItem("Stop Open To Lan") {
							{
								// addActionListener(e -> server.StopOpenToLan());
							}
						});
						add(new JMenuItem("Close Server") {
							{
								addActionListener(e -> server.Stop());
							}
						});
					}
				});

				add(new JMenu("Reset") {
					{
						add(new JMenuItem("Resetta Partita") {
							{
								addActionListener(e -> ResetAll());
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

		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);
		frame.setResizable(false);
		server.Start(true);
		readThread.start();
	}

	// methods (GUI)
	private void GeneraTabella() {
		for(int i = 0; i < tabellone.getTabella().length; i++) {
			int pos = i;
			caselle[pos] = new JButton(String.valueOf(pos + 1));
			caselle[pos].setPreferredSize(dim);
			caselle[pos].setBorder(blackline);
			caselle[pos].setBackground(Color.WHITE);
			caselle[pos].setForeground(Color.BLACK);
			caselle[pos].setFocusable(false);
			caselle[pos].setEnabled(true);
			caselle[pos].setFont(new Font("Roboto", Font.BOLD, 15));
			caselle[pos].addActionListener(l -> CheatNumero(l));
			caselle[pos].addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent evt) {
					if(!(caselle[pos].getBackground().equals(Color.BLACK)))
						caselle[pos].setBackground(color);
				}

				public void mouseExited(MouseEvent evt) {
					if(!(caselle[pos].getBackground().equals(Color.BLACK)))
						caselle[pos].setBackground(Color.WHITE);
				}

				public void mouseReleased(MouseEvent evt) {
					caselle[pos].setBackground(Color.BLACK);
					caselle[pos].setForeground(Color.WHITE);
				}
			});
			buttonPanel.add(caselle[pos]);
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

			OwnedMessage<MessageType> msg = new OwnedMessage<MessageType>(MessageType.NewNumber, num);
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

		OwnedMessage<MessageType> msg = new OwnedMessage<MessageType>(MessageType.NewNumber, num);
		server.MessageAllClients(msg, null);
	}

	public void ReadFromClient() {
		while(retry) {
			if(server.Incoming().count() > 0) {
				// System.out.println("[" + id + " \"" + server.getClient(id).getName() + "\"" + "] Says: " + msg.toString());
				Message<MessageType> msg = server.Incoming().popBack().getMsg();
				int id = msg.getId();
				switch (msg.getHeadId()) {
					case SetName:
						String s = new String();
						s = msg.Get(s);
						OwnedMessage<MessageType> msg1 = new OwnedMessage<MessageType>(MessageType.SetName);
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

					case Disconnect:
						server.getClient(id).Disconnect();
						server.removeClient(id);
						break;
				
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
		dialog.setLocationRelativeTo(frame);
		dialog.add(scroll);

		dialog.setVisible(true);

		for (Connection<MessageType> conn : server.getConnections()) {
			dlm.add(conn.getId(), conn.getName());
		}

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if(evt.getClickCount() == 2) {
					server.MessageClient(server.getClient(list.getSelectedIndex()), new OwnedMessage<MessageType>(MessageType.GetTabella));
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