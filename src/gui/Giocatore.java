package gui;

import javax.swing.*;

import game.Tabella;
import net.Client;
import net.Message;
import net.MessageType;

import java.awt.event.*;
import java.awt.*;

public class Giocatore extends JFrame {
	private static Client player;
	private JList<String> list;
	private static DefaultListModel<String> serverList;

	private JPanel centerPanel;
	private JPanel leftPanel;
	private static JLabel numeroLabel;

	private static int numero;
	private static Tabella tabella;

	public Giocatore(JFrame frame) {
		// new
		player = new Client();

		centerPanel = new JPanel();
		leftPanel = new JPanel();

		numeroLabel = new JLabel("Numero");
		
		// init
		dialogLanServer();

		// setters (frame)
		setSize(700, 450);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Tombola");
		setJMenuBar(new JMenuBar() {
			{
				
				this.setBackground(new Color(16, 7, 232));
				this.setForeground(Color.WHITE);
				add(new JMenu("File") {
					{
						this.setBackground(new Color(16, 7, 232));
						this.setForeground(Color.WHITE);
						add(new JMenuItem("Exit") {
							{
								this.setBackground(new Color(16, 7, 232));
								this.setForeground(Color.WHITE);
								addActionListener(e -> frame.dispose());
							}
						});
					}
				});

				add(new JMenu("Server") {
					{
						this.setBackground(new Color(16, 7, 232));
						this.setForeground(Color.WHITE);
						add(new JMenuItem("Connect To Server") {
							{
								addActionListener(e -> dialogConnectToServer());
							}
						});
						add(new JMenuItem("List Server") {
							{
								addActionListener(e -> dialogLanServer());
							}
						});
						add(new JMenuItem("Disconect") {
							{
								this.setBackground(new Color(16, 7, 232));
								this.setForeground(Color.WHITE);
								addActionListener(e -> player.DisconnectFromServer());
							}
						});
					}
				});

				add(new JMenu("Giocatore") {
					{
						add(new JMenuItem("Nome") {
							{
								addActionListener(l -> addName());
							}

							private void addName() {
								player.setName("Kevin");
								Message msg = new Message(MessageType.SetName, "Kevin");
								player.Send(msg);
							}
						});
					}
				});
			}
			
		});


		// add (centerPanel)
		centerPanel.add(new Cartella());
		centerPanel.add(new Cartella());
		centerPanel.add(new Cartella());

		// add (leftPanel)
		leftPanel.add(numeroLabel);

		// set (centerPanel)
		centerPanel.setBackground(Color.red);
		centerPanel.setPreferredSize(new Dimension(100, 100));

		// set (leftPanel)
		leftPanel.setPreferredSize(new Dimension(100, 100));
		leftPanel.setBackground(Color.BLUE);

		// add (frame)
		add(centerPanel, BorderLayout.CENTER);
		add(leftPanel, BorderLayout.WEST);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(true);
			}
		});
	}

	// methods
	private void ShowWindow() {
		setVisible(true);
	}

	// methods (net)
	private void dialogLanServer() {
		final int _WIDTH = 450;
		final int _HEIGHT = 222;

		JDialog dialog = new JDialog();
		JTextField name = new JTextField();
		JLabel nameLabel = new JLabel("Username");
		JScrollPane scroll = new JScrollPane(list);

		serverList = new DefaultListModel<String>();
		list = new JList<String>(serverList);

		player.StartLanSearch();
		serverList = new DefaultListModel<String>();
		
		nameLabel.setBounds((_WIDTH / 2 - 90) - 80, 0, 120, 30);
		name.setHorizontalAlignment(JTextField.HORIZONTAL);
		name.setBounds(_WIDTH / 2 - 90, 0, 180, 30);

		list = new JList<String>(serverList);
		scroll.setBounds(0, 31, _WIDTH, 100);

		dialog.setSize(_WIDTH, _HEIGHT);
		dialog.setLayout(null);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setResizable(false);
		dialog.setVisible(true);

		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				player.StopLanSearch();
				ShowWindow();
			}
		});
		
		dialog.add(nameLabel);
		dialog.add(name);
		dialog.add(scroll);
		dialog.add(new JButton("Connetti") {
			{
				addActionListener(l -> Connect());
				setBounds((_WIDTH / 2) - 75, 132, 150, 60);
			}

			private void Connect() {
				if(list.getSelectedValuesList().size() > 0) {
					if(name.getText() != "Username") {
						player.setName(name.getText());
						System.out.println("Name assigned: " + player.getName() + ", pending server approval.");
					}
					else
						System.out.println("Skipping name set");

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
					player.ConnectToServer(host, Integer.valueOf(port));
				} else {
					System.out.println("No servers found.");
				}
			}
		});
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
				numero = Integer.valueOf(msg.getBody());
				numeroLabel.setText(msg.getBody());
				tabella.AddNumber(numero);
				break;

			case LAN_SERVER_DISCOVEY:
				if(!serverList.contains(msg.getBody()))
					serverList.add(0, msg.getBody());
				break;
			
			case SetName:
				if(msg.getBody() == "true")
					System.out.println("Name successfully set.");
				break;
		
			default:
				System.out.println("[NET] Error: \"" + msg.getHead() + "\" is not a valid command.");
				break;
		}
	}
}
