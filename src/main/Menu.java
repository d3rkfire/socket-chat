package main;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import content.Properties.UserType;

public class Menu {

	private JFrame frame;
	private JTextField txtName;
	private JButton btnJoin, btnHost;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Menu window = new Menu();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Menu() {
		initialize();
		btnJoin.addActionListener(joinAL);
		btnHost.addActionListener(hostAL);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Menu");
		frame.setBounds(100, 100, 197, 151);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 14, 46, 14);
		frame.getContentPane().add(lblName);
		
		txtName = new JTextField("Sebille");
		txtName.setBounds(66, 11, 100, 20);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);
		
		btnJoin = new JButton("Join");
		btnJoin.setBounds(40, 39, 89, 23);
		frame.getContentPane().add(btnJoin);
		
		btnHost = new JButton("Host");
		btnHost.setBounds(40, 73, 89, 23);
		frame.getContentPane().add(btnHost);
	}
	
	private ActionListener joinAL = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String ipAddress = "127.0.0.1";
			ipAddress = JOptionPane.showInputDialog(null, "Please input the server ip address:", "IP Address", JOptionPane.PLAIN_MESSAGE);
			
			if (ipAddress != null) {
				JFrame chat = new Chat(UserType.Client, txtName.getText(), ipAddress);
				chat.setVisible(true);
			}
			frame.dispose();
		}
	};
	private ActionListener hostAL = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFrame chat = new Chat(UserType.Server, txtName.getText());
			chat.setVisible(true);
			frame.dispose();
		}
	};
}
