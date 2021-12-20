import java.awt.*;
import javax.swing.*;

public class LayoutEg1 extends JPanel{
    private static final int ROWS = 10;

    public LayoutEg1() {
        setLayout(new GridBagLayout());
        for (int i = 0; i < ROWS; i++) {
            GridBagConstraints gbc = makeGbc(0, i);
            JLabel label = new JLabel("Row Label " + (i + 1));
            add(label, gbc);

            JPanel panel = new JPanel();
            panel.add(new JCheckBox("check box"));
            panel.add(new JTextField(10));
            panel.add(new JButton("Button"));
            panel.setBorder(BorderFactory.createEtchedBorder());
            gbc = makeGbc(1, i);
            add(panel, gbc);
        }
    }

    private GridBagConstraints makeGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = x;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = (x == 0) ? GridBagConstraints.LINE_START : GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("Layout Eg1");
        JPanel statusPanel = new JPanel();
        frame.getContentPane().add(new LayoutEg1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(statusPanel, BorderLayout.SOUTH);
        statusPanel.add(new JLabel("Ciao"));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }
}