package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientReceiver extends Thread {
	private ServerSocket server;
	private String name;
	public Callback callback;
	public ClientReceiver(ServerSocket server, String name) {
		this.server = server;
		this.name = name;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket client = server.accept();
				
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				out.println(name);
				
				callback.addSocket(client);
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static interface Callback {
		public void addSocket(Socket client);
	}
}
