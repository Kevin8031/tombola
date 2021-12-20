import javax.swing.*;
import java.awt.event.*;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.util.Scanner;

public class Giocatore extends Tabella {
	private JFrame frame;
	private JPanel panel1;
	private JPanel panel2;
	private GridLayout griglia;
	private JButton[] caselle;
	private JLabel labelNumero;
	private JPanel statusPanel;

	private int numero;

	private Socket socket;
	private Scanner inputStream;
	private PrintStream outputStream;
	private Thread rThread;

	Giocatore(JFrame parent) {
		numero = 0;
		caselle = new JButton[RIGHE * COLONNE];
		frame = new JFrame("Tombola");
		labelNumero = new JLabel("-Numero-");
		panel1 = new JPanel();
		panel2 = new JPanel();
		griglia = new GridLayout(3, 9, 3, 3);
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
								addActionListener(e -> Disconnect());
							}
						});
					}
				});
			}
		});
		
		frame.setLocationRelativeTo(null);
		// frame.setResizable(false);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Disconnect();
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

	private void dialogConnectToServer() {
		JDialog dialog = new JDialog(frame);
		dialog.setLocationRelativeTo(frame);

		JTextField host = new JTextField();
		JTextField port = new JTextField();

		dialog.setLayout(new GridLayout());

		dialog.add(host);
		dialog.add(port);
		dialog.add(new JButton("Connetti") {
			{
				addActionListener(e -> ConnectToServer(host.getText(), Integer.valueOf(port.getText())));
			}
		});
		
		dialog.setSize(300, 60);
		dialog.setVisible(true);
	}
	
	private boolean ConnectToServer(String host, int port) {
		try {
			socket = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
			socket.connect(socketAddress);
			inputStream = new Scanner(socket.getInputStream());
			outputStream = new PrintStream(socket.getOutputStream());
			rThread = new Thread(() -> {ReadNumber();});
			rThread.start();
			System.out.println(socket.toString());
			
			return !socket.isClosed();
		} catch (IOException e) {
			System.err.println(e);
			return false;
		}
	}

	private void Disconnect() {
		if(socket != null) {
			try {
				socket.close();
				rThread.join(100);
				System.out.println("Disconnesso");
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		else
			System.out.println("Already disconnected!");
	}

	private void ReadNumber() {
		numero = inputStream.nextInt();
		System.out.println(numero);
		labelNumero.setText(String.valueOf(numero));
		numeriEstratti.add(numero);
		ReadNumber();
	}

	private void ControllaNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		
		if(btn.getText().equals(String.valueOf(numero))) {
			btn.setBackground(Color.BLACK);
			btn.setForeground(Color.WHITE);

			Combo combo = CheckCombo();
			System.out.println(combo.toString());
			outputStream.println(Net.CheckCombo.toString() + combo);
		}
	}
}