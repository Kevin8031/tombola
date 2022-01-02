package gui;

import javax.swing.*;

import game.Tabellone;
import net.Message;
import net.MessageType;
import net.Server;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

public class Master extends Tabellone {
    // attributes (GUI)
	private JFrame frame;
	private JPanel centerPanel;
	private JPanel leftPanel;
	private JPanel topPanel;

	private JPanel panel1;
	private JPanel buttonPanel;
	private JLabel numero;
	private GridLayout griglia;
	private Tabellone tabellone;
	private JButton[] caselle;
	
	private static Server host;

	public Master(JFrame parent) {
		host = new Server();
		caselle = new JButton[90];
		numeriEstratti = new ArrayList<Integer>(90);
		frame = new JFrame("Tombola");
		griglia = new GridLayout(9, 10, 5, 5);
		panel1 = new JPanel();
		numero = new JLabel("-Numero Uscito-");
		buttonPanel = new JPanel();
		tabellone = new Tabellone();
		frame.setSize(600, 350);
		frame.setLayout(new BorderLayout());

		centerPanel = new JPanel();
		leftPanel = new JPanel();
		topPanel = new JPanel();
		
		
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
								addActionListener(e -> host.StartServer());
							}
						});
						add(new JMenuItem("Start Open To Lan") {
							{
								addActionListener(e -> host.StartOpenToLan());
							}
						});
						add(new JMenuItem("Stop Open To Lan") {
							{
								addActionListener(e -> host.StopOpenToLan());
							}
						});
						add(new JMenuItem("Close Server") {
							{
								addActionListener(e -> host.StopServer());
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
				host.StopServer();
				parent.setVisible(true);
			}
		});

		frame.setVisible(true);
		host.StartServer();
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

			Message msg = new Message(MessageType.NewNumber, String.valueOf(num));
			host.Send(msg);
		}
		else {
			new JOptionPane("Gioco Finito") {
				{
					showMessageDialog(this, "Gioco Finito");
				}
			};
		}
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

		host.getClients().forEach((k, v) -> {
			dlm.add(0, v.getId() + " " + v.getName());
		});
	}

	private void CheatNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();

		int num = Integer.valueOf(btn.getText());

		numeriEstratti.add(num);
		System.out.println("Selected number [" + num + "]. " + numeriEstratti.size() + "/" + 90);

		numero.setText(String.valueOf(num));
		caselle[num - 1].setBackground(Color.BLACK);
		caselle[num - 1].setForeground(Color.WHITE);

		Message msg = new Message(MessageType.NewNumber, String.valueOf(num));
		host.Send(msg);
	}

	public static void ReadFromClient(int id, Message msg) {
		System.out.println("[" + id + " \"" + host.getClient(id).getName() + "\"" + "] Says: " + msg.toString());

		switch (MessageType.valueOf(msg.getHead())) {
			case SetName:
				String s = new String(msg.getBody());
				if(s.length() > 0) {
					host.getClient(id).setName(s);
					System.out.println("Name set for: " + id + " \"" + host.getClient(id).getName() + "\"");
					host.getClient(id).Send(new Message(MessageType.SetName, "true"));
				} else
					host.getClient(id).Send(new Message(MessageType.SetName, "Il nome non può essere vuoto"));
				break;

			case Disconnect:
				host.getClients().get(id).DisconnectFromServer();
				break;
		
			default:
				break;
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
}