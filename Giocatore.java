import javax.swing.*;
import java.awt.event.*;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.Scanner;

public class Giocatore extends Tabella {
    private JFrame frame;
    private JPanel panel1;
    private JPanel panel2;
    private GridLayout griglia;
    private Tabella tabella;
    private JButton[] caselle;
    private JLabel labelNumero;

    private int numero;

    private Socket socket;
    private MulticastSocket multicastSocket;
    private Thread rThread;
    private Thread multicastThread;
    private byte[] buf;

    Giocatore(JFrame parent) {
        numero = 0;
        caselle = new JButton[90];
        frame = new JFrame("Tombola");
        labelNumero = new JLabel("-Numero-");
        panel1 = new JPanel();
        panel2 = new JPanel();
        griglia = new GridLayout(3, 9, 3, 3);
        tabella = new Tabella();
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
    }

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
                        caselle[j + tabella.RIGHE * i].setBackground(new Color(16, 7, 232));
                        caselle[j + tabella.RIGHE * i].setForeground(Color.WHITE);
                        caselle[j + tabella.RIGHE * i].setFocusable(false);
                        caselle[j + tabella.RIGHE * i].setFont(new Font("Roboto", Font.BOLD, 20));
                        caselle[j + tabella.RIGHE * i].addActionListener(e -> ControllaNumero(e));
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
    
    private boolean ConnectToServer(String host, int port) {
        try {
            socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
            socket.connect(socketAddress);
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
        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            numero = scanner.nextInt();
            System.out.println(numero);
            labelNumero.setText(String.valueOf(numero));
            ReadNumber();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

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

    private String WaitResponse(NetworkInterface nic) {
        try {
            System.out.println(NetworkInterface.getNetworkInterfaces());
            NetworkInterface nicc = NetworkInterface.getByName("wlan1");
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

        System.out.println(btn.getText() + " == " + String.valueOf(numero) + ": " +  btn.getText() == String.valueOf(numero));
        
        if(btn.getText() == String.valueOf(numero)) {
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.WHITE);
        }
    }
}