import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.DisplayMode;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JComboBox;

public class Client extends JFrame {

	String IP;
	Socket clientSocket;
	String sentence;
	String response;
	BufferedReader inFromUser;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	String history;

	public Client(String host, int port) {

		try {
			this.clientSocket = new Socket(host, port);
			setVisible(true);
			setBounds(100, 100, 450, 300);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().setLayout(null);

			display = new JLabel("type here");
			display.setVerticalAlignment(SwingConstants.TOP);
			display.setHorizontalAlignment(SwingConstants.LEFT);
			display.setBounds(296, 16, 124, 34);
			getContentPane().add(display);
			
			textArea = new JTextArea();
			textArea.setBounds(12, 13, 262, 181);
			getContentPane().add(textArea);
			textArea.setText("");

			input = new JTextField();
			input.setBounds(296, 63, 124, 43);
			getContentPane().add(input);
			input.setColumns(10);

			JButton joinButton = new JButton("Join");
			joinButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					boolean loggedIn = false;
					try {
						while (true) {
							name = input.getText();
							if (!name.equals("")) {
								Join(name);
								input.setText("");
								inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
								response = inFromServer.readLine();
								System.out.println(response);
							
								history = textArea.getText() + '\n' + response;				
								textArea.setText(history);
								
								if (response.equals("Server: Successful")) {
									loggedIn = true;
									//display.setText(name);
									setTitle(name);
									break;
								}
							} else {
								break;
							}
						}
						if (loggedIn == true) {
							//send.start();
							receive.start();
							joinButton.disable();
							joinButton.setVisible(false);
							sendButton.enable();
							sendButton.setVisible(true);
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			joinButton.setBounds(314, 207, 97, 25);
			getContentPane().add(joinButton);

			System.out.println("ready to connect on port: " + port + " and IP: " + host);
			textArea.setText("ready to connect on port: " + port + " and IP: " + host + '\n' + "Please Enter a Name:");
			history = "";
			sendButton = new JButton("Send");
			sendButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					sentence = input.getText();
					input.setText("");			
					
					System.out.println("test: " + sentence);
					try {
						outToServer = new DataOutputStream(clientSocket.getOutputStream());
						if (sentence.equals("members"))
							GetMemberList();
						else
							outToServer.writeBytes(sentence + '\n');
						if (sentence.equals("QUIT")) {
							clientSocket.close();
							receive.stop();
							//send.stop();
							sendButton.disable();
							sendButton.setVisible(false);
							input.disable();
							input.setVisible(false);
							textArea.setText("Bye");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				

				}
			});
			sendButton.setBounds(314, 207, 97, 25);
			getContentPane().add(sendButton);
			
			sendButton.disable();
			sendButton.setVisible(false);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

/*	public Thread send = new Thread() {
		@SuppressWarnings("deprecation")
		public void run() {
			while (true) {
				// inFromUser = new BufferedReader(new InputStreamReader(System.in));
				try {
					outToServer = new DataOutputStream(clientSocket.getOutputStream());
					// sentence inFromUser.readLine();
					if (sentence.equals("members"))
						GetMemberList();
					else
						outToServer.writeBytes(sentence + '\n');
					if (sentence.equals("QUIT")) {
						clientSocket.close();
						receive.stop();
						send.stop();
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}; */

	public Thread receive = new Thread() {
		public void run() {
			while (true) {
				try {
					inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					response = inFromServer.readLine();
					System.out.println(response);
					history = textArea.getText() + '\n' + response;
					textArea.setText(history);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	};
	private JTextField input;
	private JLabel display;
	private String name;
	private JButton sendButton;
	private JTextArea textArea;
	

	public void Join(String name) throws IOException {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes("Join:" + name + '\n');
	}

	public void GetMemberList() throws IOException {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes("members" + '\n');
	}

	public static void main(String args[]) {
		Client c1 = new Client("localhost", 6000); //in button join server and checks the port
	}
}
