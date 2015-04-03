package network;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import utils.SerializeMessage;

public class Receiver extends Thread {
	private Socket socket = null;
	private ServerSocket serverSocket;
	private ConsistencyProtocol protocol;
	int id;
	public boolean finished;
	
	public Receiver(int id, ServerSocket serverSocket, ConsistencyProtocol protocol) {
		this.serverSocket = serverSocket;
		this.id = id;
		this.socket = null;
		this.protocol = protocol;
		this.finished = false;
	}
	
	public void run() {
		System.out.println("Receiver " + id + " started");
		try {
			this.socket = serverSocket.accept();
			System.out.println("Peer accepted new incoming connection");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			DataInputStream stream = new DataInputStream(this.socket.getInputStream());

			while(!finished) {
				int size = stream.readInt();
				//System.out.println("SIZE" + size);
				byte[] bytes = new byte[size];
				stream.readFully(bytes);
				AbstractMessage message = (AbstractMessage)SerializeMessage.deserialize(bytes);
				//System.out.println("Receiver " + this.id + " got message " + message.toString());
				protocol.receive(message);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public boolean acceptedConnection() {
		return !(this.socket == null);
	}
	
	public void kill()
	{
		finished = true;
		try {
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}
