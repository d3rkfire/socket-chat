package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientListener extends Thread {
	public Socket client;
	public Callback callback;
	
	public ClientListener(Socket client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			while(true) {
				String line = in.readLine();
				callback.addMessage(line);
			}
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static interface Callback {
		public void addMessage(String line);
	}
}
