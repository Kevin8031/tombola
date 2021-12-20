import javax.swing.*;
import java.awt.event.*;

import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.IOException;

public class Giocatore extends Tabella {
    // attributes
    private JFrame frame;
    private JPanel panel1;
    private JPanel panel2;
    private JLabel numero;
    private Tabella tabella;
    private GridLayout griglia;
    private Socket socket;
    private JButton[] caselle;

    // constructor
    Giocatore(JFrame parent) {
        frame = new JFrame("Tombola");
        panel1 = new JPanel();
        panel2 = new JPanel();
        numero = new JLabel("-Numero-");
        tabella = new Tabella();
        griglia = new GridLayout(3, 9, 3, 3);
        socket = new Socket();
        caselle = new JButton[90];

        frame.setSize(600, 350);
        frame.setLayout(new GridLayout(2,1));
        
        numero.setVerticalAlignment(JLabel.BOTTOM);
        numero.setHorizontalTextPosition(JLabel.CENTER);
        numero.setVerticalTextPosition(JLabel.CENTER);
        numero.setForeground(Color.WHITE);
        numero.setFont(new Font("Roboto", Font.BOLD, 36));

        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(74, 0, 255));
        panel1.add(numero);

	private boolean[] combo;

        tabella.generaTabella();
        
        GeneraTabella();
        
        frame.add(panel1);
        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
                                this.setBackground(new Color(16, 7, 232));
                                this.setForeground(Color.WHITE);
                                addActionListener(e -> ConnectToServer());
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
        frame.setVisible(true);
    }

    // methods (GUI)
    private void GeneraTabella() {
        for(int i = 0; i < tabella.RIGHE; i++) 
                for(int j = 0; j < tabella.COLONNE; j++) {
                    caselle[j + tabella.RIGHE * i] = new JButton();
                    if (tabella.getTabella(i + tabella.RIGHE * j) == -1) {
                        panel2.add(caselle[j + tabella.RIGHE * i]); 
                        caselle[j + tabella.RIGHE * i].setBackground(new Color(16, 7, 232));
                        caselle[j + tabella.RIGHE * i].setFocusable(false);
                    }
                    else {
                        caselle[j + tabella.RIGHE * i].setText(String.valueOf(tabella.getTabella(i + tabella.RIGHE * j)));
                        panel2.add(caselle[j + tabella.RIGHE * i]);
                        caselle[j + tabella.RIGHE * i].setFont(new Font("Roboto", Font.BOLD, 36));
                        caselle[j + tabella.RIGHE * i].setBackground(new Color(16, 7, 232));
                        caselle[j + tabella.RIGHE * i].setForeground(Color.WHITE);
                        caselle[j + tabella.RIGHE * i].setFocusable(false);
                    }
            }
    }

    // methods (Networking)
    private void ConnectToServer() {
        try {
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 60001);
            socket.connect(socketAddress);
            new Thread(() -> {ReadNumber();}).start();
            System.out.println(socket.toString());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

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
		frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
		statusPanel.add(new JLabel("Ciao"));

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

	//TODO: LAN SEARCH
	private void SearchGame() {
		String message = "Cerco Partita";
		ByteBuffer buf = ByteBuffer.wrap(message.getBytes());
		try {
			System.out.println("Searching lan game - port(4321)");
			DatagramChannel datagramChannel = DatagramChannel.open();
			NetworkInterface nic = NetworkInterface.getByIndex(1);
			InetSocketAddress inet = InetSocketAddress.createUnresolved("230.0.0.0", 4321);

			datagramChannel.bind(null);
			datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);
			datagramChannel.send(buf, inet);

			System.out.println("Multicast sent: " + message);
			System.out.println("Waiting response...");
			String response = WaitResponse(nic);
			System.out.println("Message recived: " + response);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	//TODO: LAN SEARCH
	private String WaitResponse(NetworkInterface nic) {
		try {
			System.out.println(NetworkInterface.getNetworkInterfaces());
			//NetworkInterface nicc = NetworkInterface.getByName("wlan1");
			String message = "MiConnetto";
			buf = message.getBytes();
			multicastSocket = new MulticastSocket();
			InetAddress group = InetAddress.getByName("230.0.0.0");
			multicastSocket.joinGroup(group);

			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4321);
			multicastSocket.send(packet);


			// DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
			// datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			// datagramChannel.bind(new InetSocketAddress(4321));
			// datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);


			// MembershipKey membershipKey = datagramChannel.join(inetAddress, nic);
			// ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			// datagramChannel.read(byteBuffer);
			// byteBuffer.flip();
			// byte[] b = new byte[byteBuffer.limit()];
			// byteBuffer.get(b, 0, byteBuffer.limit());
			// membershipKey.drop();
			// return new String(b);
		} catch (Exception e) {
			System.err.println(e);
		}
		return "failed";   
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