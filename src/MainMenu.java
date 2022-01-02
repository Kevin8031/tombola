import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import gui.Giocatore;
import gui.Master;

public class MainMenu extends JFrame {
	// constants
	private final Font FONT = new Font("Roboto", Font.BOLD, 25);
	private final Color color = new Color(192, 202, 202);

	// attributes
	private Border blackline;
	//private Cartella giocatore;
	//private Master master;
	private Image image;

	// constructor
	MainMenu() {
		blackline = BorderFactory.createLineBorder(Color.BLACK);
		image = Toolkit.getDefaultToolkit().getImage("C://Users//arman//Desktop//GITHUB//Repos//tombola//src//icon.png");

		setIconImage(image);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Tombola - Main Menu");
		setSize(350, 200);
		setLayout(new GridLayout(1, 2, 0, 0));
		setLocationRelativeTo(null);
		setResizable(false);

		add(new JButton("Gioca") {
			{
				setBorder(blackline);
				setFont(FONT);
				setBackground(Color.WHITE);
				setForeground(Color.BLACK);
				setFocusable(false);
				addActionListener(e -> Giocatore());
				addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent evt) {
						setBackground(color);
					}
				
					public void mouseExited(MouseEvent evt) {
						setBackground(Color.WHITE);
					}
				});
			}
		});

		add(new JButton("Host Server") {
			{
				setBorder(blackline);
				setFont(FONT);
				setBackground(Color.WHITE);
				setForeground(Color.BLACK);
				setFocusable(false);
				addActionListener(e -> Master());
				addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent evt) {
						setBackground(color);
					}
				
					public void mouseExited(MouseEvent evt) {
						setBackground(Color.WHITE);
					}
				});
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
