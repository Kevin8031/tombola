import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

    Giocatore() {
        caselle = new JButton[90];
        socket = new Socket();
        frame = new JFrame("Tombola");
        numero = new JLabel("-Numero-");
        panel1 = new JPanel();
        panel2 = new JPanel();
        griglia = new GridLayout(3, 9, 3, 3);
        tabella = new Tabella();
        frame.setSize(600, 350);
        frame.setLayout(new GridLayout(2,1));
        
        numero.setBackground(Color.RED);
        numero.setForeground(Color.BLACK);
        numero.setHorizontalAlignment(SwingConstants.CENTER);
        numero.setVerticalAlignment(SwingConstants.BOTTOM);
        numero.setVisible(true);
        panel1.add(numero);

        panel2.setBackground(Color.GRAY);
        panel2.setLayout(griglia);

        tabella.generaTabella();
        
        GeneraTabella();
        
        frame.add(panel1);
        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(new JMenuBar() {
            {
                add(new JMenu("File") {
                    {
                        add(new JMenuItem("Exit") {
                            {
                                addActionListener(e -> frame.dispose());
                            }
                        });
                    }
                });

                add(new JMenu("Server") {
                    {
                        add(new JMenuItem("Connect To Server") {
                            {
                                addActionListener(e -> ConnectToServer());
                            }
                        });
                        add(new JMenuItem("Disconect") {
                            {
                                addActionListener(e -> Disconnect());
                            }
                        });
                    }
                });
            }
        });
        
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void GeneraTabella() {
        for(int i = 0; i < tabella.RIGHE; i++) 
                for(int j = 0; j < tabella.COLONNE; j++) {
                    caselle[j + tabella.RIGHE * i] = new JButton();
                    if (tabella.getTabella(i + tabella.RIGHE * j) == -1)
                        panel2.add(caselle[j + tabella.RIGHE * i]);
                    else {
                        caselle[j + tabella.RIGHE * i].setText(String.valueOf(tabella.getTabella(i + tabella.RIGHE * j)));
                        panel2.add(caselle[j + tabella.RIGHE * i]);
                    }
            }
    }
    
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