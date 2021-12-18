import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.event.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.net.*;
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
    private ArrayList<Socket> client;

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
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                parent.setVisible(true);
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
        try {
            serverSocket = new ServerSocket(60001);
            new Thread(() -> Accept()).start();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void StopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println(e);
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
            client.add(serverSocket.accept());
            System.out.println(client.get(0).toString());
            Accept();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}