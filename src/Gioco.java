package src;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.*;

public class Gioco extends JFrame {
	Giocatore giocatore;
	Master master;

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

		/*addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}

			@Override
			public void windowActivated(WindowEvent e) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}

		});*/
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
