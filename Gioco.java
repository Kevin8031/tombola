import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

public class Gioco extends JFrame {
    Giocatore giocatore;
    Master master;

    enum playerType {
        giocatore,
        tabellone
    }

    Gioco() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tombola - Main Menu");
        setSize(350, 200);
        setLayout(new GridLayout(1, 2));
        setLocationRelativeTo(null);

        add(new JButton("Gioca") {
            {
                addActionListener(e -> Giocatore());
            }
        });

        add(new JButton("Host Server") {
            {
                addActionListener(e -> Master());
            }
        });

        setVisible(true);
    }
    
    private void Master() {
        new Master(this);
        setVisible(false);
    }

    private void Giocatore() {
        new Giocatore(this);
        setVisible(false);
    }
}
