package network;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import node.Node;
import command.Command;

public abstract class ConsistencyProtocol {
	public Integer id;
	public Node node;
	public long deliveredMessages2;	
	
	public ConsistencyProtocol(int id, Node node) {
		this.id = id;
		this.node = node;
	}
	
	public abstract void broadcast(ArrayList<Sender> senders, String line);
	
	public abstract void receive(AbstractMessage message);
	
	public abstract void updateFromMessage(AbstractMessage abstractMessage);
	
	public abstract void deliver(AbstractMessage message);
			
	public synchronized void updateGUI(Command command) {
		System.out.println("Updating GUI " + command.toString());		
		String newText = "";
		String oldText = node.textArea.getText();
		if (command.type.compareTo(Command.INSERT) == 0) {
			newText = oldText.substring(0,command.pos) + command.c + oldText.substring(command.pos);
		} else if (command.type.compareTo(Command.DELETE) == 0) {
			newText = oldText.substring(0,command.pos) + oldText.substring(command.pos + 1);
		} else {
			return;
		}
		
		System.out.println("New text " + newText);
		node.textArea.setText(newText);
		node.textArea.update(node.textArea.getGraphics());
		this.deliveredMessages2+=1;
	}
}
