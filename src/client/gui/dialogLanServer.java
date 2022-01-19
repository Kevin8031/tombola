package client.gui;

import javax.swing.*;
import java.awt.event.*;

import net.Client;

public class dialogLanServer {
	public <T> dialogLanServer(JFrame parent, DefaultListModel<String> serverList, Client<T> client) {
		final int _WIDTH = 450;
		final int _HEIGHT = 224;

		JFrame frame = new JFrame();
		JTextField name = new JTextField();
		JLabel nameLabel = new JLabel("Username");
		
		JList<String> list = new JList<String>(serverList);
		JScrollPane scroll = new JScrollPane(list);

		client.StartLanSearch();

		nameLabel.setBounds((_WIDTH / 2 - 90) - 80, 1, 120, 30);
		name.setHorizontalAlignment(JTextField.HORIZONTAL);
		name.setBounds(_WIDTH / 2 - 90, 1, 180, 30);

		scroll.setBounds(0, 32, _WIDTH, 100);

		frame.setSize(_WIDTH, _HEIGHT);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				parent.setVisible(true);
			}
		});

		frame.add(nameLabel);
		frame.add(name);
		frame.add(scroll);
		frame.add(new JButton("Connetti") {
			{
				addActionListener(l -> Connect());
				setBounds((_WIDTH / 2) - 75, 133, 150, 60);
			}

			private void Connect() {
				if(list.getSelectedValuesList().size() > 0) {
					client.setName(name.getText());
					System.out.println("Name assigned: " + client.getName() + ", pending server approval.");

					String s = list.getSelectedValue();
					int i = 0;
					int j;
					while (i < s.length() - 1) {
						if(s.charAt(i++) == ' ')
							break;
					}

					j = i;
					while (i < s.length() - 1) {
						if(s.charAt(i++) == ' ')
							break;
					}
					String port = s.substring(j, i - 1);

					String host = s.substring(i, s.length());
					if(client.Connect(host, Integer.valueOf(port))) {
						client.StopLanSearch();
						parent.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(frame, "Impossibile connettersi al server", "Errore", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Selezionare un server", "Errore", JOptionPane.ERROR_MESSAGE);
					System.out.println("No servers selected.");
				}
			}
		});

		frame.setVisible(true);
	}
}
