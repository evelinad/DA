package cbcast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import abcast.QueueEntry;
import command.Command;
import network.AbstractMessage;
import network.ConsistencyProtocol;
import network.Sender;
import node.Node;

public class CBCASTProtocol extends ConsistencyProtocol {
	public ArrayList<Long> timestamps;
	private ArrayList<AbstractMessage> delayQueue;  
	
	public CBCASTProtocol(int id, Node node) {
		super(id, node);
		this.delayQueue = new ArrayList<AbstractMessage>();		
		this.timestamps = new ArrayList<Long>(Collections.nCopies(node.maxPeers, 0L));
	}

	@Override
	public synchronized void receive(AbstractMessage abstractMessage) {
		CBCASTMessage cbcastMessage = (CBCASTMessage) abstractMessage;
		System.out.println("Node " + id + " received " + cbcastMessage);
		updateFromMessage(abstractMessage);
	}

	@Override
	public synchronized void broadcast(ArrayList<Sender> senders, String line) {
		CBCASTMessage cbcastMessage = new CBCASTMessage(new Command(line), timestamps, id);
		this.timestamps.set(id, timestamps.get(id) + 1);
		System.out.println("Broadcasting " + cbcastMessage + " to nodes...");
		for (Sender sender: senders) {
			sender.send(cbcastMessage);
		}
		
		updateFromMessage(cbcastMessage);
	}

	@Override
	public void deliver(AbstractMessage message) {
		CBCASTMessage cbcastMessage = (CBCASTMessage) message;
		System.out.println("Message delivered: " + cbcastMessage);
		updateGUI(cbcastMessage.command);
		updateTimeVector(cbcastMessage.timestamps);		
	}

	public void updateTimeVector(ArrayList<Long> timestamps) {
		System.out.println("Updating vector time..." + " Current timestamp: " + this.timestamps.toString() + " Timestamp from message: " + timestamps.toString());
		for (int i = 0; i < timestamps.size(); i++) {
			this.timestamps.set(i, Math.max(timestamps.get(i), this.timestamps.get(i)));
		}
		System.out.println("Current timestamp updated to: " + this.timestamps.toString());
	}
	
	public boolean needsToBeDelayed(AbstractMessage abstractMessage) {
		CBCASTMessage message = (CBCASTMessage)abstractMessage;
		ArrayList<Long> timestampsFromMessage = message.timestamps;
		if (timestampsFromMessage.get(message.sender) != (this.timestamps.get(message.sender) + 1) &&
				message.sender != this.id) {
			return true;
		}
		
		for (int i = 0; i < timestampsFromMessage.size(); i++) {
			if (i != message.sender && message.sender != this.id) {
				if (timestampsFromMessage.get(i) > this.timestamps.get(i)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public synchronized void updateFromMessage(AbstractMessage abstractMessage) {
		CBCASTMessage cbcastMessage = (CBCASTMessage)abstractMessage;
		System.out.println("Updating from " + cbcastMessage + " ...");
		
		if (needsToBeDelayed(cbcastMessage)) {
			System.out.println(cbcastMessage + "needs to be delayed");
			delayQueue.add(cbcastMessage);
			Collections.sort(delayQueue, new TimestampVectorComparator());			
		} else {
			deliver(cbcastMessage);
		}
		
		System.out.println("Trying to deliver other messages ...");
		boolean atLeastOneDelivered = true;
		while(atLeastOneDelivered) {
			atLeastOneDelivered = false;
			ArrayList<AbstractMessage> temp = new ArrayList<AbstractMessage>();			
			for (AbstractMessage message: delayQueue) {
				CBCASTMessage cbcastMessageTemp = (CBCASTMessage)message;
				if (!needsToBeDelayed(cbcastMessageTemp)) {
					temp.add(message);
					deliver(cbcastMessageTemp);
					atLeastOneDelivered = true;
				}
			}
			
			for (AbstractMessage msg: temp) {
				delayQueue.remove(msg);
				Collections.sort(delayQueue, new TimestampVectorComparator());				
			}
		}
	}
}
