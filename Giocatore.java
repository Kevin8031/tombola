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

        panel2.setBackground(new Color(74, 0, 255));
        panel2.setLayout(griglia);

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