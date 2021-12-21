
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

public class Master extends Tabellone {
    // attributes (GUI)
	private JFrame frame;
	private JPanel panel1;
	private JPanel panel2;
	private JLabel numero;
	private GridLayout griglia;
	private Tabellone tabellone;
	private JButton[] caselle;
	
	private static Server host;

	Master(JFrame parent) {
		host = new Server();
		caselle = new JButton[90];
		numeriEstratti = new ArrayList<Integer>(90);
		frame = new JFrame("Tombola");
		griglia = new GridLayout(9, 10, 5, 5);
		panel1 = new JPanel();
		numero = new JLabel("-Numero Uscito-");
		panel2 = new JPanel();
		tabellone = new Tabellone();
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
								addActionListener(e -> host.StartServer());
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
		JList<String> jList = new JList<String>(dlm);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setSize(150, 150);

		dialog.add(jList);

		dialog.setVisible(true);

		for (Client i : host.getClients()) {
			dlm.add(0, i.getSocket().getRemoteSocketAddress().toString());
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

		Message msg = new Message(MessageType.NewNumber, String.valueOf(num));
		host.Send(msg);
	}

	public static void ReadFromClient(int id, Message msg) {
		System.out.println("[" + id + " \"" + host.getClient(id).getName() + "\"" + "] Says: " + msg.toString());

		switch (MessageType.valueOf(msg.getHead())) {
			case NewNumber:
				host.getClient(id).setName(msg.getBody());
				System.out.println("Name set for: " + id + " \"" + host.getClient(id).getName() + "\"");
				break;
		
			default:
				break;
		}
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