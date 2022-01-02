package gui;
import javax.swing.*;

import game.Combo;
import game.Tabella;

import java.awt.*;
import java.awt.event.*;

public class Cartella extends JPanel {
	final private int RIGHE = Tabella.RIGHE;
	final private int COLONNE = Tabella.COLONNE;
	final private Font FONT = new Font("Roboto", Font.BOLD, 36);

	// attributes
	private static int numero;
	private static Tabella tabella;

	// attributes (GUI)
	private static JLabel labelNumero;
	private JButton[] caselle;

	// constructor
	public Cartella(JFrame parent) {
		tabella = new Tabella(); 
		labelNumero = new JLabel("- NUMERO -");
		caselle = new JButton[RIGHE * COLONNE];

		labelNumero.setVerticalAlignment(JLabel.BOTTOM);
		labelNumero.setHorizontalTextPosition(JLabel.CENTER);
		labelNumero.setVerticalTextPosition(JLabel.CENTER);
		labelNumero.setForeground(Color.WHITE);
		labelNumero.setFont(FONT);

		setBackground(Color.white);
		setLayout(new GridLayout(3, 9, 3, 3));
		
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
					caselle[i + RIGHE * j] = new JButton();
					if (tabella.getTabella(i + RIGHE * j) == -1) {
						add(caselle[i + RIGHE * j]); 
						caselle[i + RIGHE * j].setBackground(new Color(16, 7, 232));
						caselle[i + RIGHE * j].setFocusable(false);
						caselle[i + RIGHE * j].setName(String.valueOf(i + RIGHE * j));
					}
					else {
						caselle[i + RIGHE * j].setText(String.valueOf(tabella.getTabella(i + RIGHE * j)));
						add(caselle[i + RIGHE * j]);
						caselle[i + RIGHE * j].setBackground(new Color(16, 7, 232));
						caselle[i + RIGHE * j].setForeground(Color.WHITE);
						caselle[i + RIGHE * j].setFocusable(false);
						caselle[i + RIGHE * j].setFont(new Font("Roboto", Font.BOLD, 20));
						caselle[i + RIGHE * j].addActionListener(e -> ControllaNumero(e));
						caselle[i + RIGHE * j].setName(String.valueOf(i + RIGHE * j));
					}
			}
	}


}