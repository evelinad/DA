package node;

import gui.GUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import abcast.ABCASTProtocol;
import cbcast.CBCASTProtocol;
import command.Command;
import threephase.ReviseMessage;
import threephase.ThreePhaseProtocol;
import utils.Testing;
import network.ConsistencyProtocol;
import network.Receiver;
import network.Sender;

public class Node extends GUI {
	private static final long serialVersionUID = 7526472295622776147L;
	public static final int CONN_TIMEOUT = 3000;
	private ArrayList<Sender> senders;
	private ArrayList<Receiver> receivers;
	private ConsistencyProtocol protocol;
	private ServerSocket serverSocket;
	private int id;
	private String IP;
	private int port;
	public int maxPeers;
	
	public Node(int id, final int maxPeers, ArrayList<String> ipArray, ArrayList<Integer> portArray, String protocolType)
	{
		super(id, protocolType);
		senders = new ArrayList<Sender>();
		receivers = new ArrayList<Receiver>();
		this.id = id;
		this.maxPeers = maxPeers;
		System.out.println("Node " + id);
		
		try {
			this.serverSocket = new ServerSocket(portArray.get(id));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		
		switch (protocolType) {
			case "threephase": {
				this.protocol = new ThreePhaseProtocol(id, this, senders);
				break;
			}
			case "cbcast": {
				this.protocol = new CBCASTProtocol(id, this);
				break;
			}
			default: {
				if (protocolType.startsWith("abcast")) {
					this.protocol = new ABCASTProtocol(id, Integer.parseInt(protocolType.replace("abcast ", "")), this, senders);
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}		
		
		for (int i = 0; i < this.maxPeers; i++) {
			try {
				if (id != i) {
					Receiver receiver = new Receiver(i, this.serverSocket, this.protocol);
					receiver.start();
					receivers.add(receiver);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}		
		
		
		for (int i = 0; i < this.maxPeers; i++) {
			try {
				if (id != i) {
					Sender sender = new Sender(i, ipArray.get(i), portArray.get(i));
					sender.start();
					senders.add(sender);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
	}
	
	public void finish()
	{
		for (int i = 0; i < senders.size(); i++) {
			senders.get(i).kill();
		}
		
		for (int i = 0; i < receivers.size(); i++) {
			receivers.get(i).kill();
		}
		try {
			serverSocket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	
	boolean connectedToAllPeers()
	{
		for (int i = 0; i < senders.size(); i++) {
			if (senders.get(i).isConnected() == false)
				return false;
		}
		
		for (int i = 0; i < receivers.size(); i++) {
			if (receivers.get(i).acceptedConnection() == false)
				return false;
		}
		
		return true;
	}
	
	public long deliveredMessages()
	{
		return this.protocol.deliveredMessages2;
	}
	
	public String getText()
	{
		return this.textArea.getText();
	}
	@Override
	public void run() {
		while (connectedToAllPeers() == false) {
			try {
				System.out.println("Waiting to complete the connection to remote peers ...");
				Thread.sleep(CONN_TIMEOUT);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		
		System.out.println("Connected to all peers ...");
		
		try {
			Thread.sleep(CONN_TIMEOUT);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader("/home/evelina/workspace/DA/src/" + "node" + id));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println("New message to be sent: " + line);
				this.protocol.broadcast(this.senders, line);
				Thread.sleep(Testing.randomFromInterval(100, 500));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

}
