package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import content.Properties;
import content.Properties.UserType;
import main.ClientReceiver.Callback;

@SuppressWarnings("serial")
public class Chat extends JFrame {

	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea txtChat;
	private JScrollPane scrollPane;
	private JButton btnSend;
	private DefaultListModel<String> lstOnline = new DefaultListModel<>();

	private UserType type;
	private String name;

	private ServerSocket server;
	private ArrayList<ServerClient> serverClients = new ArrayList<>();

	private Socket client;
	/**
	 * Create the frame.
	 * @wbp.parser.constructor
	 */
	public Chat(UserType type, String name) {
		// Login as Server
		this.type = type;
		this.name = name;
		initialize();
		btnSend.addActionListener(sendAL);
		try {
			server = new ServerSocket(Properties.port);
			lstOnline.addElement(name);

			ClientReceiver clientReceiver = new ClientReceiver(server, name);
			clientReceiver.callback = new Callback() {
				@Override
				public void addSocket(Socket client) {
					String name = "";
					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						name = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
					}

					ClientListener clientListener = new ClientListener(client);
					clientListener.callback = new ClientListener.Callback() {
						@Override
						public void addMessage(String line) {
							if (line.contains("[DISCONNECT]")) {
								for (int i = 0; i < serverClients.size(); i++)
									if (serverClients.get(i).socket == client) {
										lstOnline.removeElement(serverClients.get(i).name);
										serverClients.get(i).clientListener.interrupt();
										serverClients.remove(i);
									}

								String nameList = "[NAMELIST],";
								for (int i = 0; i < lstOnline.size(); i++)
									nameList += lstOnline.getElementAt(i)+",";
								forwardToClients(nameList);
							}
							else {
								txtChat.append(line + "\n");
								scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
								forwardToClients(line);
							}
						}
					};
					clientListener.start();

					System.out.println(name + " has joined.");
					serverClients.add(new ServerClient(name, client, clientListener));
					lstOnline.addElement(name);

					String nameList = "[NAMELIST],";
					for (int i = 0; i < lstOnline.size(); i++)
						nameList += lstOnline.getElementAt(i)+",";
					forwardToClients(nameList);
				}
			};
			clientReceiver.start();

		} catch (IOException e) {e.printStackTrace();}
	}
	public Chat(UserType type, String name, String ip) {
		// Login as Client
		this.type = type;
		this.name = name;
		initialize();
		btnSend.addActionListener(sendAL);

		try {
			client = new Socket(ip, Properties.port);

			this.addWindowListener(new WindowListener() {
				@Override
				public void windowOpened(WindowEvent arg0) {}
				@Override
				public void windowIconified(WindowEvent arg0) {}
				@Override
				public void windowDeiconified(WindowEvent arg0) {}
				@Override
				public void windowDeactivated(WindowEvent arg0) {}
				@Override
				public void windowClosing(WindowEvent arg0) {
					if (type == UserType.Client) {
						PrintWriter out;
						try {
							out = new PrintWriter(client.getOutputStream(), true);
							out.println("[DISCONNECT]");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				@Override
				public void windowClosed(WindowEvent arg0) {}
				@Override
				public void windowActivated(WindowEvent arg0) {}
			});
			
			String serverName = "";
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				serverName = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lstOnline.addElement(serverName);
			lstOnline.addElement(name);

			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			out.println(name);

			ServerListener serverListener = new ServerListener(client);
			serverListener.callback = new ServerListener.Callback() {
				@Override
				public void addMessage(String line) {
					if (line.contains("[NAMELIST],")) {
						System.out.println(line);
						String[] nameList = line.split(",");

						lstOnline.clear();
						for (int i = 1; i < nameList.length; i++) {
							lstOnline.addElement(nameList[i]);
						}
					}
					else {
						txtChat.append(line + "\n");
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					}
				}
			};
			serverListener.start();
		} catch (IOException e) {
			System.out.println("Cannot join server.");
			e.printStackTrace();
		}
	}

	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		this.setTitle("Chat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 395);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtChat = new JTextArea();
		scrollPane = new JScrollPane(txtChat);
		txtChat.setEditable(false);
		txtChat.setLineWrap(true);
		scrollPane.setBounds(10, 11, 300, 300);
		contentPane.add(scrollPane);

		JLabel lblOnlineFriends = new JLabel("Online friends:");
		lblOnlineFriends.setBounds(320, 11, 104, 14);
		contentPane.add(lblOnlineFriends);

		btnSend = new JButton("Send");
		btnSend.setBounds(320, 323, 104, 23);
		contentPane.add(btnSend);

		txtMessage = new JTextField();
		txtMessage.setBounds(10, 324, 300, 23);
		txtMessage.addActionListener(sendAL);
		contentPane.add(txtMessage);
		txtMessage.setColumns(10);

		JList<String> jListOnline = new JList<String>(lstOnline);
		jListOnline.setBounds(320, 36, 104, 275);
		contentPane.add(jListOnline);
	}

	private ActionListener sendAL = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String message = name + ": " + txtMessage.getText();
			if (type == UserType.Server) {
				txtChat.append(message + "\n");
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
				forwardToClients(message);
			}
			else if (type == UserType.Client) {
				try {
					PrintWriter out = new PrintWriter(client.getOutputStream(), true);
					out.println(message);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			txtMessage.setText(null);
		}
	};

	private void forwardToClients(String line) {
		for (ServerClient sc : serverClients) {
			try {
				PrintWriter out = new PrintWriter(sc.socket.getOutputStream(), true);
				out.println(line);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
}
