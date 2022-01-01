package gui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Testtone extends JPanel {
	
	Testtone() {
		setPreferredSize(new Dimension(100, 100));
		add(new JLabel("Ciao") {
			{
				setBounds(0, 0, 50, 50);
			}
		});
	}
}
