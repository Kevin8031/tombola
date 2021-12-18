import javax.swing.*;
import java.awt.event.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
import java.nio.channels.*;
import java.nio.*;
import java.io.*;

public class Master extends Tabellone {
    private JFrame frame;
    private JPanel panel1;
    private JLabel numero;
    private JPanel panel2;
    private GridLayout griglia;
    private Tabellone tabellone;
    private ArrayList<Integer> numeriUsciti;
    private JButton[] caselle;
    
    private ServerSocket serverSocket;
    private MulticastSocket multicastSocket;
    private ArrayList<Socket> client;
    private Thread accThread;

    Master(JFrame parent) {
        client = new ArrayList<Socket>();
        caselle = new JButton[90];
        numeriUsciti = new ArrayList<Integer>(90);
        frame = new JFrame("Tombola");
        griglia = new GridLayout(3, 9, 5, 5);
        panel1 = new JPanel();
        numero = new JLabel("-Numero Ucito-");
        panel2 = new JPanel();
        tabellone = new Tabellone();
        frame.setSize(450, 300);
        frame.setLayout(new GridLayout(2,1));

        //panel1.setBounds(0, 0, 450, 150);
        panel1.setBackground(Color.gray);
        panel1.add(new JButton("Genera Numero") {
            {
                addActionListener(e -> GeneraNumero());
            }
        });
        panel1.add(numero);
        panel1.setLayout(new GridLayout());


        //panel2.setBounds(0, 150, 450, 300);
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
                                
                            }
                        });
                    }
                });

                add(new JMenu("Server") {
                    {
                        add(new JMenuItem("Start Server") {
                            {
                                addActionListener(e -> StartServer());
                            }
                        });
                        add(new JMenuItem("Close Server") {
                            {
                                addActionListener(e -> StopServer());
                            }
                        });
                    }
                });

                add(new JMenu("Partita") {
                    {
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
                StopServer();
                parent.setVisible(true);
            }

            public void windowOpened(WindowEvent e) {
                Inizio();
            }
        });
        
        frame.setVisible(true);
    }

    private void GeneraTabella() {
        for(int i = 0; i < tabellone.getTabella().length; i++) {
            caselle[i] = new JButton(String.valueOf(i + 1));
            panel2.add(caselle[i]);
        }
    }

    public void GeneraNumero() {
        Random random = new Random();
        int num = 0;

        if(numeriUsciti.size() < 90) {
            do {
                num = random.nextInt(90) + 1;
            } while (numeriUsciti.indexOf(num) != -1);

            numeriUsciti.add(num);
            System.out.println("numeriUsciti.size: " + numeriUsciti.size());

            numero.setText(String.valueOf(num));
            caselle[num - 1].setBackground(Color.BLACK);
            caselle[num - 1].setForeground(Color.WHITE);

            SendNumber(num);
        }
        else {
            new JOptionPane("Gioco Finito") {
                {
                    showMessageDialog(this, "Gioco Finito");
                }
            };
        }
    }

    private void StartServer() {
        if(serverSocket == null) {
            try {
                serverSocket = new ServerSocket(60001);
                accThread = new Thread(() -> Accept());
                System.out.println("Server started!");
                accThread.start();
                OpenToLan();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        else {
            System.out.println("Server already started.");
        }
    }

    private void StopServer() {
        if(serverSocket != null) {
            try {
                for (Socket i : client) {
                    i.close();
                }

                accThread.join(100);
                serverSocket.close();
                System.out.println("Server Fermato");
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        else {
            System.out.println("Cannot stop server. Server already stopped!");
        }
    }

    private void SendNumber(int number) {
        PrintStream ps;
        try {
            for (Socket c : client) {
                ps = new PrintStream(c.getOutputStream());
                ps.println(number);
            }
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void Accept() {
        try {
            System.out.println("Waiting for client to connect...");
            client.add(serverSocket.accept());
            System.out.println("Client connected: " + client.get(0).toString());
            System.out.println("No. of clients: " + client.size());
            Accept();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void Inizio() {
        JDialog dialog = new JDialog(frame, "Benvenuto");

        dialog.setSize(150, 150);

        dialog.add(new JLabel("Benvenuto a tombola!\nUsa il menù Server in alto per gestire la partita"));
        
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    };

    private void ListClients() {
        JDialog dialog = new JDialog(frame, "Clients");
        DefaultListModel<String> dlm = new DefaultListModel<String>();
        JList<String> jList = new JList<String>(dlm);
        
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(150, 150);
        
        dialog.add(jList);

        dialog.setVisible(true);
        
        for (Socket i : client) {
            dlm.add(0, i.getRemoteSocketAddress().toString());
        }
    }

    private void OpenToLan() {
        try {
            NetworkInterface nic = NetworkInterface.getByIndex(1);
            DatagramChannel datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);
            datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            datagramChannel.bind(new InetSocketAddress(4321));
            datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, nic);

            InetAddress inetAddress = InetAddress.getByName("230.0.0.0");

            MembershipKey membershipKey = datagramChannel.join(inetAddress, nic);
            System.out.println("Server opened to lan - port(4321)");
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            datagramChannel.read(byteBuffer);
            byteBuffer.flip();
            byte[] b = new byte[byteBuffer.limit()];
            byteBuffer.get(b, 0, byteBuffer.limit());
            membershipKey.drop();
            System.out.println("Message: " + b);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}