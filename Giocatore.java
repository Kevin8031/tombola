import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Giocatore extends Tabella {
	// attributes (GUI)
	private JFrame frame;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel statusPanel;
	private JLabel labelNumero;
	private GridLayout griglia;
	private JButton[] caselle;
	private int numero;

	private Player player;

	// constructor
	Giocatore(JFrame parent) {
		frame = new JFrame("Tombola");
		panel1 = new JPanel();
		panel2 = new JPanel();
		griglia = new GridLayout(3, 9, 3, 3);
		labelNumero = new JLabel("- NUMERO -");
		caselle = new JButton[RIGHE * COLONNE];
		player = new Player();
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
				dialogConnectToServer();
			}
		});
		/*
		try {
			WaitResponse(NetworkInterface.getByIndex(1));
			
		} catch (Exception e) {
			System.err.println(e);
		}*/
		//frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		//statusPanel.add(new JLabel("Ciao"));

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
		JDialog dialog = new JDialog(frame);
		dialog.setLocationRelativeTo(frame);

		JTextField host = new JTextField();
		JTextField port = new JTextField("60001");

		dialog.setLayout(new GridLayout());

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

	private void ControllaNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		
		if(btn.getText().equals(String.valueOf(numero))) {
			btn.setBackground(Color.BLACK);
			btn.setForeground(Color.WHITE);

			Combo combo = CheckCombo();
			System.out.println(combo.toString());
			player.Send(Net.CheckCombo.toString() + combo);
		}
	}
}