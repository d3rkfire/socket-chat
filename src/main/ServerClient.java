package main;

import java.net.Socket;

public class ServerClient {
	public Socket socket;
	public String name;
	public ClientListener clientListener;
	
	public ServerClient(String name, Socket socket, ClientListener clientListener) {
		this.socket = socket;
		this.name = name;
		this.clientListener = clientListener;
	}
}