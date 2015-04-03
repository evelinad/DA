package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import utils.SerializeMessage;

public class Sender extends Thread {
	public static final int CONN_TIMEOUT = 3000;
	public String IP;
	public int port;
	public int id;
	private boolean connected;
	private Socket socket;
	private boolean finished;
	
	public Sender(int id, String IP, int port) {
		this.IP = IP;
		this.id = id;
		this.port = port;
		connected = false;
		finished = false;
	}
	
	public void run() {
		System.out.println("Sender " + id + " started");
		while (!connected && !finished) {
			try {
				Thread.sleep(CONN_TIMEOUT);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			
			System.out.println("Trying to connect to " + IP + " port " + port);
			
			try {
				socket = new Socket(IP, port);
				connected = true;
				System.out.println("Connected to " + IP + " port " + port);
			} catch (Exception exc) {
				connected = false;
			}
			
		}
		
		while (!finished);
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void send(AbstractMessage message) {
		if (!connected) {
			return;
		}
		try {
			byte[] serialized = SerializeMessage.serialize(message);
			int size = serialized.length;
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeInt(size);
			dos.write(serialized);	
		} catch (Exception e) {
			connected = false;
		}		
	}
	
	public void kill()
	{
		finished = true;
		try {
			if (connected) {
				this.socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}
