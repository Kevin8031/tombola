import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;

public class Giocatore extends Tabella {
    JFrame frame;
    JPanel panel1;
    JPanel panel2;
    GridLayout griglia;
    Tabella tabella;

    Giocatore() {
        frame = new JFrame("Tombola");
        griglia = new GridLayout(3, 9, 5, 5);
        panel1 = new JPanel();
        panel2 = new JPanel();
        tabella = new Tabella();
        frame.setSize(450, 300);
        frame.setLayout(new GridLayout(2,1));

        //panel1.setBounds(0, 0, 450, 150);
        panel1.setBackground(Color.gray);
        //panel2.setBounds(0, 150, 450, 300);
        panel2.setBackground(Color.darkGray);
        panel2.setLayout(griglia);

        tabella.generaTabella();
        // for(int i = 0; i < 15; i++) {
        //     panel2.add(new JButton(String.valueOf(i)));
        // }
        
        for(int i = 0; i < tabella.RIGHE; i++) 
                for(int j = 0; j < tabella.COLONNE; j++) {
                    if (tabella.getTabella(i + tabella.RIGHE * j) == -1)
                        panel2.add(new JButton(""));
                    else
                        panel2.add(new JButton(String.valueOf(tabella.getTabella(i + tabella.RIGHE * j))));
            }

        frame.add(panel1);
        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
}