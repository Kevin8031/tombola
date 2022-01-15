package client.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import net.Client;
import net.Message;
import net.MessageType;
import net.OwnedMessage;
public class Giocatore extends JFrame {
	// constants
	// final private Color CENTER_BACKGROUND = new Color(77, 168, 235);
	// private final Color WEST_BACKGROUND = new Color(235, 202, 66);	
	private final Font FONT = new Font("Roboto", Font.BOLD, 20);

	// attributes
	private int num;
	private DefaultListModel<String> serverList;
	private DefaultListModel<Integer> numberList;
	private Client<MessageType> client;
	private ArrayList<Cartella> cartelle;
	private ArrayList<Integer> numeriEstratti;
	private ActionListener read;
	private Timer readTimer;

	// attributes (GUI)
	private JFrame parent;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private Image icon;
	private JList<Integer> list;
	private JScrollPane numList;
	private JLabel numeroLabel;

	// constructor
	public Giocatore(JFrame parent) {
		this.parent = parent;

		client = new Client<MessageType>();
		read = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ReadFromServer();
			}
		};
		readTimer = new Timer(75, read);
		numberList = new DefaultListModel<Integer>();
		list = new JList<Integer>(numberList);
		numList = new JScrollPane(list);
		centerPanel = new JPanel();
		leftPanel = new JPanel();
		numeroLabel = new JLabel("Numeri", SwingConstants.CENTER);
		cartelle = new ArrayList<Cartella>();
		numeriEstratti = new ArrayList<Integer>();
		icon = Toolkit.getDefaultToolkit().getImage("res/icon.png");

		// init
		serverList = new DefaultListModel<String>();
		new dialogLanServer(this, serverList, client);
		
		// setters (numeroLabel)
		numeroLabel.setPreferredSize(new Dimension(120, 30));
		numeroLabel.setBackground(Color.YELLOW);
		numeroLabel.setFont(FONT);
		
		// setters (frame)
		setIconImage(icon);
		setSize(700, 445);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(5, 5));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Tombola");

		// MenuBar
		setJMenuBar(new JMenuBar() {
			{
				add(new JMenu("File") {
					{
						add(new JMenuItem("Exit") {
							{
								addActionListener(e -> parent.dispose());
							}
						});
					}
				});

				add(new JMenu("Server") {
					{
						add(new JMenuItem("Connect To Server") {{ addActionListener(e -> dialogConnectToServer()); }});

						add(new JMenuItem("List Server") {{ addActionListener(e -> dialogLanServer()); }});

						add(new JMenuItem("Disconect") {
							{
								addActionListener(e -> Disconnect());
							}
						});
					}
				});
				
				add(new JMenu("Giocatore") { { add(new JMenuItem("Cambia nome") {{ addActionListener(l -> setUserName()); }}); } });
			}
			
		});

		// add (centerPanel)
		cartelle.add(0, new Cartella(this));
		cartelle.get(0).setNumeriEstratti(numeriEstratti);
		centerPanel.add(cartelle.get(0));

		cartelle.add(0, new Cartella(this));
		cartelle.get(0).setNumeriEstratti(numeriEstratti);
		centerPanel.add(cartelle.get(0));
		
		cartelle.add(0, new Cartella(this));
		cartelle.get(0).setNumeriEstratti(numeriEstratti);
		centerPanel.add(cartelle.get(0));

		// set (centerPanel)
		centerPanel.setPreferredSize(new Dimension(100, 100));
		centerPanel.setBackground(Color.WHITE);

		// set (leftPanel)
		leftPanel.setPreferredSize(new Dimension(120, 445));
		leftPanel.setBackground(Color.YELLOW);
		leftPanel.setLayout(new BorderLayout());

		// add (leftPanel)
		leftPanel.add(numeroLabel, BorderLayout.NORTH);
		leftPanel.add(numList, BorderLayout.CENTER);

		// add (frame)
		add(centerPanel, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Disconnect();
				parent.setVisible(true);
			}
		});
	}

	// methods
	private void ShowWindow(JFrame frame) {
		frame.dispose();
		client.StopLanSearch();
		setVisible(true);
	}

	// methods (net)
	private void setUserName() {
		JDialog dialog = new JDialog();
		JTextField name = new JTextField();
		JLabel label = new JLabel("Username");

		label.setBounds(15, 5, 120, 30);
		name.setBounds(150 - 70, 5, 120, 30);
		dialog.add(label);
		dialog.add(name);
		dialog.add(new JButton("OK") {
			{
				addActionListener(l -> _setName());
				setBounds(205, 5, 70, 30);
			}
			
			public void _setName() {
				String s = new String(name.getText());
				if(s.length() > 0) {
						client.setName(name.getText());
						System.out.println("Name assigned: " + s + ", pending server approval.");
						OwnedMessage<MessageType> msg = new OwnedMessage<MessageType>(MessageType.SetName, client.getName());
						client.Send(msg);
						dialog.dispose();
					} else {
					JOptionPane.showMessageDialog(dialog, "Nessun nome inserito", "Errore", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		dialog.setSize(300, 80);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setLayout(null);
		dialog.setResizable(false);
		dialog.setTitle("Cambia Username");
		dialog.setVisible(true);
	}

	private void dialogConnectToServer() {
		JDialog dialog;
		dialog = new JDialog();
		dialog.setLocationRelativeTo(this);

		dialog.setLayout(new GridLayout());

		JTextField host = new JTextField();
		JTextField port = new JTextField("4321");

		dialog.add(host);
		dialog.add(port);
		dialog.add(new JButton("Connetti") { { addActionListener(e -> Connect()); }
			private void Connect() {
				client.Connect(host.getText(), Integer.valueOf(port.getText()));
			}
		});

		dialog.setSize(300, 60);
		dialog.setVisible(true);
	}

	public void ReadFromServer() {
		if(client.Incoming().count() > 0) {
			Message<MessageType> msg = client.Incoming().popFront().getMsg();
			System.out.println("[Server] Says: " + msg.toString());

			switch (msg.getHeadId()) {
				case NewNumber:
					num = msg.Get(num);
					numeriEstratti.add(num);
					numberList.add(0, num);
					for (Cartella cartella : cartelle) {
						cartella.setNum(num);
					}
					break;

				case LAN_SERVER_DISCOVEY:
					String serverName = null;
					serverName = msg.Get(serverName);
					int port = 0;
					port = msg.Get(port);
					int playerCount = 0;
					playerCount = msg.Get(playerCount);
					String host = null;
					host = msg.Get(host);

					String entry = new String(serverName + " " + host + ":" + port + " Giocatori: " + playerCount);
					
					if(!serverList.contains(entry))
						serverList.add(0, entry);
					break;
				
				case SetName:
					String s = new String();
					if(s.equals("true"))
						System.out.println("Name successfully set.");
					else {
						System.out.println("Cannot set name: " + s);
						JOptionPane.showMessageDialog(null, "Nome assegnato dal server: " + s, "Info", JOptionPane.INFORMATION_MESSAGE);
						client.setName(s);
					}
					break;
				
				case GetTabella:
					System.out.println("[Server] Asked for table. Sending table to server.");
					SendTabella();
					break;
			
				default:
					System.out.println("[NET] Error: \"" + msg.getHeadId() + "\" is not a valid command.");
					break;
			}
		}
	}

	private void SendTabella() {
		OwnedMessage<MessageType> msg = new OwnedMessage<MessageType>(MessageType.GetTabella);
		msg.Add(cartelle);
		client.Send(msg);
	}

	private void Disconnect() {
		client.Send(new OwnedMessage<MessageType>(MessageType.Disconnect));
		readTimer.stop();
		client.Disconnect();
	}
}
