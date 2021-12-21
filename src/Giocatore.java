import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Giocatore extends Tabella {
	// attributes (GUI)
	private JFrame frame;
	private JPanel panel1;
	private JPanel panel2;
	// private JPanel statusPanel;
	private static JLabel labelNumero;
	private GridLayout griglia;
	private JButton[] caselle;
	private static int numero;
	private static DefaultListModel<String> serverList;

	private static Client player;


	// constructor
	Giocatore(JFrame parent) {
		frame = new JFrame("Tombola");
		panel1 = new JPanel();
		panel2 = new JPanel();
		griglia = new GridLayout(3, 9, 3, 3);
		labelNumero = new JLabel("- NUMERO -");
		caselle = new JButton[RIGHE * COLONNE];
		player = new Client();
		//statusPanel = new JPanel();


		frame.setSize(600, 350);
		frame.setLayout(new GridLayout(2,1));
		
		labelNumero.setVerticalAlignment(JLabel.BOTTOM);
		labelNumero.setHorizontalTextPosition(JLabel.CENTER);
		labelNumero.setVerticalTextPosition(JLabel.CENTER);
		labelNumero.setForeground(Color.WHITE);
		labelNumero.setFont(new Font("Roboto", Font.BOLD, 36));

		panel1.setLayout(new GridBagLayout());
		panel1.setBackground(new Color(74, 0, 255));
		panel1.add(labelNumero);

		panel2.setBackground(new Color(74, 0, 255));
		panel2.setLayout(griglia);

		generaTabella();
		
		GeneraTabella();
		
		frame.add(panel1);
		frame.add(panel2);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setJMenuBar(new JMenuBar() {
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
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				player.DisconnectFromServer();
				parent.setVisible(true);
			}

			public void windowOpened(WindowEvent e) {
				dialogLanServers();
			}

			
		});
	}

	// methods (GUI)
	private void GeneraTabella() {
		for(int i = 0; i < RIGHE; i++) 
				for(int j = 0; j < COLONNE; j++) {
					caselle[i + RIGHE * j] = new JButton();
					if (getTabella(i + RIGHE * j) == -1) {
						panel2.add(caselle[i + RIGHE * j]); 
						caselle[i + RIGHE * j].setBackground(new Color(16, 7, 232));
						caselle[i + RIGHE * j].setFocusable(false);
						caselle[i + RIGHE * j].setName(String.valueOf(i + RIGHE * j));
					}
					else {
						caselle[i + RIGHE * j].setText(String.valueOf(getTabella(i + RIGHE * j)));
						panel2.add(caselle[i + RIGHE * j]);
						caselle[i + RIGHE * j].setBackground(new Color(16, 7, 232));
						caselle[i + RIGHE * j].setForeground(Color.WHITE);
						caselle[i + RIGHE * j].setFocusable(false);
						caselle[i + RIGHE * j].setFont(new Font("Roboto", Font.BOLD, 20));
						caselle[i + RIGHE * j].addActionListener(e -> ControllaNumero(e));
						caselle[i + RIGHE * j].setName(String.valueOf(i + RIGHE * j));
					}
			}
	}

	// methods (Networking)
	private void dialogConnectToServer() {
		JDialog dialog;
		dialog = new JDialog(frame);
		dialog.setLocationRelativeTo(frame);

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

	private void dialogLanServers() {
		serverList = new DefaultListModel<String>();
		JDialog dialog = new JDialog(frame);
		JList<String> list = new JList<String>(serverList);
		JScrollPane scroll = new JScrollPane(list);

		dialog.setLocationRelativeTo(frame);

		dialog.setLayout(new GridLayout());

		dialog.add(scroll);
		
		dialog.setSize(300, 60);
		dialog.setVisible(true);
	}

	private void ControllaNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		
		if(btn.getText().equals(String.valueOf(numero))) {
			btn.setBackground(Color.BLACK);
			btn.setForeground(Color.WHITE);

			Combo combo = CheckCombo();
			System.out.println(combo.toString());
			player.Send(MessageType.CheckCombo.toString() + combo);
		}
	}

	public static void ReadFromServer(Message msg) {
		System.out.println("[Server] Says: " + msg.toString());
		msg.setBody(msg.getBody().trim());

		switch (MessageType.valueOf(msg.getHead())) {
			case NewNumber:
				numero = Integer.valueOf(msg.getBody());
				labelNumero.setText(msg.getBody());
				numeriEstratti.add(numero);
				break;

			case LAN_SERVER_DISCOVEY:
				String host = new String();
				String port = new String();
				serverList.add(0, msg.getBody());
				// player.ConnectToServer(host, port);
				break;
		
			default:
				break;
		}
	}
}