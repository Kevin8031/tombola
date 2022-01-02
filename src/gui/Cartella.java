package gui;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import game.Combo;
import game.Tabella;

public class Cartella extends JPanel {
	// constants
	final private Dimension dim = new Dimension(50, 41);
	final private Color color = new Color(192, 202, 202);
	final private int RIGHE = Tabella.RIGHE;
	final private int COLONNE = Tabella.COLONNE;
	final private Font FONT = new Font("Roboto", Font.BOLD, 36);
	
	// attributes
	private static int numero;
	private static Tabella tabella;
	
	// attributes (GUI)
	private static JLabel labelNumero;
	private JButton[] caselle;
	private Border blackline;

	// constructor
	public Cartella(JFrame parent) {
		tabella = new Tabella(); 
		labelNumero = new JLabel("- NUMERO -");
		caselle = new JButton[RIGHE * COLONNE];
		blackline = BorderFactory.createLineBorder(Color.BLACK, 1);

		labelNumero.setVerticalAlignment(JLabel.BOTTOM);
		labelNumero.setHorizontalTextPosition(JLabel.CENTER);
		labelNumero.setVerticalTextPosition(JLabel.CENTER);
		labelNumero.setForeground(Color.WHITE);
		labelNumero.setFont(FONT);

		setBackground(Color.WHITE);
		setLayout(new GridLayout(3, 9, -1, -1));
		setBackground(Color.WHITE);

		tabella.generaTabella();
		
		GeneraTabella();
	}

	// methods
	private void ControllaNumero(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		
		if(btn.getText().equals(String.valueOf(numero))) {
			btn.setBackground(Color.BLACK);
			btn.setForeground(Color.WHITE);

			Combo combo = tabella.CheckCombo();
			System.out.println(combo.toString());
		}
	}

	// methods (GUI)
	private void GeneraTabella() {
		for(int i = 0; i < RIGHE; i++) 
			for(int j = 0; j < COLONNE; j++) {
					int pos = i + RIGHE * j;
					caselle[pos] = new JButton();
					if (tabella.getTabella(pos) == -1) {
						add(caselle[pos]);
						caselle[pos].setPreferredSize(dim);
						caselle[pos].setBorder(blackline);
						caselle[pos].setBackground(Color.WHITE);
						caselle[pos].setFocusable(false);
						caselle[pos].setEnabled(false);
						caselle[pos].setName(String.valueOf(pos));
					}
					else {
						add(caselle[pos]);
						caselle[pos].setPreferredSize(dim);
						caselle[pos].setText(String.valueOf(tabella.getTabella(pos)));
						caselle[pos].setBorder(blackline);
						caselle[pos].setBackground(Color.WHITE);
						caselle[pos].setForeground(Color.BLACK);
						caselle[pos].setFocusable(false);
						caselle[pos].setFont(new Font("Roboto", Font.BOLD, 20));
						caselle[pos].addActionListener(e -> ControllaNumero(e));
						caselle[pos].setName(String.valueOf(pos));
						caselle[pos].addMouseListener(new MouseAdapter() {
							public void mouseEntered(MouseEvent evt) {
								caselle[pos].setBackground(color);
							}
							
							public void mouseExited(MouseEvent evt) {
								caselle[pos].setBackground(Color.WHITE);
							}
						});
					}
			}
	}

	public void setNumeriEstratti(ArrayList<Integer> numeriEstratti) {
		tabella.setNumeriEstratti(numeriEstratti);
	}
}