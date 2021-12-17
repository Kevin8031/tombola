import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.*;

public class Master extends Tabellone {
    JFrame frame;
    JPanel panel1;
    JPanel panel2;
    GridLayout griglia;
    Tabellone tabellone;

    Master() {
        frame = new JFrame("Tombola");
        griglia = new GridLayout(3, 9, 5, 5);
        panel1 = new JPanel();
        panel2 = new JPanel();
        tabellone = new Tabellone();
        frame.setSize(450, 300);
        frame.setLayout(new GridLayout(2,1));

        //panel1.setBounds(0, 0, 450, 150);
        panel1.setBackground(Color.gray);
        panel1.add(new JButton("Genra Numero") {
            {
                addActionListener(e -> GeneraNumero());
            }
        });


        //panel2.setBounds(0, 150, 450, 300);
        panel2.setBackground(Color.darkGray);
        panel2.setLayout(griglia);

        GeneraTabella();

        frame.add(panel1);
        frame.add(panel2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }

    private void GeneraTabella() {
        for(int i = 0; i < tabellone.getTabella().length; i++) {
            panel2.add(new JButton(String.valueOf(tabellone.getTabella(i))) {
                {
                    // setBackground();
                    set
                }
            });
        }
    }

    public void GeneraNumero() {

    }
}
