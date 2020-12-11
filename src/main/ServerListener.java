package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerListener extends Thread {
	private BufferedReader in;
	public Callback callback;
	
	public ServerListener(Socket client) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.in = in;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				String line = in.readLine();
				callback.addMessage(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static interface Callback {
		public void addMessage(String line);
	}
}
