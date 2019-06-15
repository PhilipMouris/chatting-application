import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUICLASS extends JFrame {

	private JPanel contentPane;
	JLabel choose;
	JComboBox comboBox;
	JButton btnSelect;
	int port = 0;
	int index = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUICLASS frame = new GUICLASS();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUICLASS() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);;
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		choose = new JLabel("Please choose a server");
		contentPane.add(choose, BorderLayout.NORTH);

		String[] servers = { "Server 1", "Server 2" };
		comboBox = new JComboBox(servers);
		contentPane.add(comboBox, BorderLayout.CENTER);

		btnSelect = new JButton("Select");
		btnSelect.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) {
				Client c;
				index = comboBox.getSelectedIndex();
				if (index == 0)
					port = 6000;
				else
					port = 8000;
				
				if (port == 6000 || port == 8000)
					c = new Client("localhost", port);
			}
		});
		contentPane.add(btnSelect, BorderLayout.SOUTH);
		
		
	}
}
