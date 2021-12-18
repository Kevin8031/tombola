import javax.swing.*;
import java.awt.event.*;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Giocatore extends Tabella {
    private JFrame frame;
    private JPanel panel1;
    private JPanel panel2;
    private GridLayout griglia;
    private Tabella tabella;
    private JButton[] caselle;
    private JLabel numero;
    private Socket socket;

    Giocatore(JFrame parent) {
        caselle = new JButton[90];
        frame = new JFrame("Tombola");
        numero = new JLabel("-Numero-");
        panel1 = new JPanel();
        panel2 = new JPanel();
        griglia = new GridLayout(3, 9, 3, 3);
        tabella = new Tabella();
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

        panel2.setBackground(new Color(74, 0, 255));
        panel2.setLayout(griglia);

        tabella.generaTabella();
        
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
                parent.setVisible(true);
            }

            public void windowOpened(WindowEvent e) {
                dialogConnectToServer();
            }
        });
    }

    private void GeneraTabella() {
        for(int i = 0; i < tabella.RIGHE; i++) 
                for(int j = 0; j < tabella.COLONNE; j++) {
                    caselle[j + tabella.RIGHE * i] = new JButton();
                    if (tabella.getTabella(i + tabella.RIGHE * j) == -1) {
                        panel2.add(caselle[j + tabella.RIGHE * i]); 
                        caselle[j + tabella.RIGHE * i].setBackground(new Color(16, 7, 232));
                        caselle[j + tabella.RIGHE * i].setForeground(Color.WHITE);
                        caselle[j + tabella.RIGHE * i].setFocusable(false);
                        caselle[j + tabella.RIGHE * i].setFont(new Font("Roboto", Font.BOLD, 36));
                    }
                    else {
                        caselle[j + tabella.RIGHE * i].setText(String.valueOf(tabella.getTabella(i + tabella.RIGHE * j)));
                        panel2.add(caselle[j + tabella.RIGHE * i]);
                        caselle[j + tabella.RIGHE * i].setBackground(new Color(16, 7, 232));
                        caselle[j + tabella.RIGHE * i].setForeground(Color.WHITE);
                        caselle[j + tabella.RIGHE * i].setFocusable(false);
                    }
            }
    }

    private void dialogConnectToServer() {
        JDialog dialog = new JDialog(frame);

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
        
        
        dialog.setSize(300, 300);
        dialog.setVisible(true);
    }
    
    private void ConnectToServer(String host, int port) {
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
            socket.connect(socketAddress);
            new Thread(() -> {ReadNumber();}).start();
            System.out.println(socket.toString());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void Disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void ReadNumber() {
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            int num = scanner.nextInt();
            System.out.println(num);
            numero.setText(String.valueOf(num));
            ReadNumber();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    
}