package gui;

import javax.swing.*;

import net.Client;
import net.Message;
import net.MessageType;

import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;

public class Giocatore extends JFrame {
	// constants
	final private Color CENTER_BACKGROUND = new Color(77, 168, 235);
	private final Color WEST_BACKGROUND = new Color(235, 202, 66);
	private final static Font FONT = new Font("Roboto", Font.BOLD, 20);

	// attributes
	private static int num;
	private static DefaultListModel<String> serverList;
	private static Client player;
	private ArrayList<Cartella> cartelle;
	private static ArrayList<Integer> numeriEstratti;

	// attributes (GUI)
	private JFrame parent;
	private JList<String> list;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private static JScrollPane numList;
	private JLabel numeroLabel;

	public Giocatore(JFrame parent) {
		this.parent = parent;
		// new
		player = new Client();
		centerPanel = new JPanel();
		leftPanel = new JPanel();
		numeroLabel = new JLabel("Numeri");
		cartelle = new ArrayList<Cartella>();
		numeriEstratti = new ArrayList<Integer>();
		numList = new JScrollPane();
		
		// init
		dialogLanServer();

		// setters
		numeroLabel.setForeground(Color.darkGray);
		numeroLabel.setFont(FONT);

		// setters (frame)
		setSize(700, 450);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout(5, 5));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Tombola");
		setJMenuBar(new JMenuBar() {
			{
				setBackground(new Color(16, 7, 232));
				setForeground(Color.WHITE);
				add(new JMenu("File") {
					{
						this.setBackground(new Color(16, 7, 232));
						this.setForeground(Color.WHITE);
						add(new JMenuItem("Exit") {
							{
								this.setBackground(new Color(16, 7, 232));
								this.setForeground(Color.WHITE);
								addActionListener(e -> parent.dispose());
							}
						});
					}
				});

				add(new JMenu("Server") {
					{
						this.setBackground(new Color(16, 7, 232));
						this.setForeground(Color.WHITE);
						add(new JMenuItem("Connect To Server") {{ addActionListener(e -> dialogConnectToServer()); }});

						add(new JMenuItem("List Server") {{ addActionListener(e -> dialogLanServer()); }});

						add(new JMenuItem("Disconect") {
							{
								this.setBackground(new Color(16, 7, 232));
								this.setForeground(Color.WHITE);
								addActionListener(e -> player.DisconnectFromServer());
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

		// add (leftPanel)
		leftPanel.add(numeroLabel);
		leftPanel.add(numList);

		// set (centerPanel)
		centerPanel.setPreferredSize(new Dimension(100, 100));
		centerPanel.setBackground(CENTER_BACKGROUND);

		// set (leftPanel)
		leftPanel.setPreferredSize(new Dimension(120, 100));
		leftPanel.setBackground(WEST_BACKGROUND);
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		// add (frame)
		add(centerPanel, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				parent.setVisible(true);
			}
		});
	}

	// methods
	private void ShowWindow(JFrame frame) {
		frame.dispose();
		player.StopLanSearch();
		setVisible(true);
	}

	// methods (net)
	private static void setUserName() {
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
						player.setName(name.getText());
						System.out.println("Name assigned: " + s + ", pending server approval.");
						Message msg = new Message(MessageType.SetName, player.getName());
						player.Send(msg);
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

	private void dialogLanServer() {
		final int _WIDTH = 450;
		final int _HEIGHT = 222;

		JFrame frame = new JFrame();
		JTextField name = new JTextField();
		JLabel nameLabel = new JLabel("Username");

		serverList = new DefaultListModel<String>();
		list = new JList<String>(serverList);
		JScrollPane scroll = new JScrollPane(list);

		player.StartLanSearch();

		nameLabel.setBounds((_WIDTH / 2 - 90) - 80, 0, 120, 30);
		name.setHorizontalAlignment(JTextField.HORIZONTAL);
		name.setBounds(_WIDTH / 2 - 90, 0, 180, 30);

		scroll.setBounds(0, 31, _WIDTH, 100);

		frame.setSize(_WIDTH, _HEIGHT);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ShowWindow(frame);
			}
		});

		frame.add(nameLabel);
		frame.add(name);
		frame.add(scroll);
		frame.add(new JButton("Connetti") {
			{
				addActionListener(l -> Connect());
				setBounds((_WIDTH / 2) - 75, 132, 150, 60);
			}

			private void Connect() {
				if(list.getSelectedValuesList().size() > 0) {
					player.setName(name.getText());
					System.out.println("Name assigned: " + player.getName() + ", pending server approval.");

					String s = list.getSelectedValue();
					int i = 0;
					int j;
					while (i < s.length() - 1) {
						if(s.charAt(i++) == ' ')
							break;
					}

					j = i;
					while (i < s.length() - 1) {
						if(s.charAt(i++) == ' ')
							break;
					}
					String port = s.substring(j, i - 1);

					String host = s.substring(i, s.length());
					if(player.ConnectToServer(host, Integer.valueOf(port))) {
						ShowWindow(frame);
					}
					else {
						parent.setVisible(true);
						JOptionPane.showMessageDialog(frame, "Impossibile connettersi al server", "Errore", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Selezionare un server", "Errore", JOptionPane.ERROR_MESSAGE);
					System.out.println("No servers selected.");
				}
			}
		});
		frame.setVisible(true);
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
		dialog.add(new JButton("Connetti") {
			{
				addActionListener(e -> player.ConnectToServer(host.getText(), Integer.valueOf(port.getText())));
			}
		});

		dialog.setSize(300, 60);
		dialog.setVisible(true);
	}

	public static void ReadFromServer(Message msg) {
		System.out.println("[Server] Says: " + msg.toString());

		switch (MessageType.valueOf(msg.getHead())) {
			case NewNumber:
				num = Integer.valueOf(msg.getBody());
				numeriEstratti.add(num);
				numList.add(new JLabel(String.valueOf(num)) {{ setFont(FONT); }});
				// leftPanel.validate();
				// leftPanel.repaint();
				break;

			case LAN_SERVER_DISCOVEY:
				if(!serverList.contains(msg.getBody()))
					serverList.add(0, msg.getBody());
				break;
			
			case SetName:
				if(msg.getBody().equals("true"))
					System.out.println("Name successfully set.");
				else {
					System.out.println("Cannot set message: " + msg.getBody());
					JOptionPane.showMessageDialog(null, msg.getBody(), "Errore", JOptionPane.ERROR_MESSAGE);
					setUserName();
				}
				break;
			
			case GetTabella:
				// TODO make it work
				System.out.println("[Server] Asked for table. Sending table to server.");
				break;
		
			default:
				System.out.println("[NET] Error: \"" + msg.getHead() + "\" is not a valid command.");
				break;
		}
	}
}
