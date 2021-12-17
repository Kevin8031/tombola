import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;

public class Gioco {
    enum giocatore {
        giocatore,
        tabellone
    }

    JFrame frame;
    JPanel panel1;
    JPanel panel2;
    GridLayout griglia;
    

    Gioco(giocatore player) {
        if(player == giocatore.giocatore)
            new FGIocatore();
        else
            new FinestraMaster();
    }

    private void FGiocatore() {
        frame = new JFrame("Tombola");
        griglia = new GridLayout(3, 9, 5, 5);
        panel1 = new JPanel();
        panel2 = new JPanel();
        frame.setSize(450, 300);
        frame.setLayout(new GridLayout(2,1));

        //panel1.setBounds(0, 0, 450, 150);
        panel1.setBackground(Color.red);
        //panel2.setBounds(0, 150, 450, 300);
        panel2.setBackground(Color.blue);
        panel2.setLayout(griglia);

        for(int i = 1; i <= 15; i++) {
            panel2.add(new JButton(String.valueOf(i)));
        }

        frame.add(panel1);
        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }

    private void FMaster() {
        
    }
}
