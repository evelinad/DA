package threephase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

import command.Command;
import network.AbstractMessage;
import network.ConsistencyProtocol;
import network.Sender;
import node.Node;
import threephase.TimestampComparator;

public class ThreePhaseProtocol extends ConsistencyProtocol {

	Long priority;
	PriorityQueue<QueueEntry> queue;
	AtomicLong timestamp;
	HashMap<String, ArrayList<Long>> messagesMap;
	ArrayList<Sender> senders;
    private final Object mutexQueue = new Object();
    private final Object mutexTimestamp = new Object();
    private final Object mutexPriority = new Object();     
	
	public ThreePhaseProtocol (int id, Node node, ArrayList<Sender> senders) {
		super(id, node);
		this.timestamp = new AtomicLong();
		this.priority = 0L;
		this.timestamp.set(0L);;
		this.messagesMap = new HashMap<String, ArrayList<Long>>();
		this.queue = new PriorityQueue<QueueEntry>(10, new TimestampComparator());
		this.senders = senders;
		File file = new File("/home/evelina/node"+ this.id);
		if (file.exists()) {
			file.delete();
		}
	}
	
	public synchronized void firstPhase(ReviseMessage message, ArrayList<Sender> senders) {
		System.out.println("Broadcasting " + message + " to nodes...");	
		synchronized (mutexQueue) {
			System.out.println("Updated priority to max from " + (this.priority + 1) + " and " + this.timestamp.get() + ": " + Math.max(this.priority + 1, this.timestamp.get()));
			this.priority = Math.max(this.priority + 1, this.timestamp.get());	
			queue.add(new QueueEntry(message, priority));
			ArrayList<Long> timestamps = messagesMap.get(message.tag);
			
			if (timestamps == null) {
				timestamps = new ArrayList<Long>();
			}
			
			timestamps.add(priority);
			System.out.println("Update proposed timestamps for " + message.tag + ": " + timestamps);			
			messagesMap.put(message.tag, timestamps);			
		}
	
		for (Sender sender: senders) {
			sender.send(message);
		}
	}
	
	public synchronized void secondPhase(ReviseMessage message) {
		System.out.println("Received " + message);
		
		synchronized (mutexQueue) {
			System.out.println("Updated priority to max from " + (this.priority + 1) + " and " + message.timestamp + ": " + Math.max(this.priority + 1, message.timestamp));
			this.priority = Math.max(this.priority + 1, message.timestamp);	
			queue.add(new QueueEntry(message, priority));	
		}
		
		Sender sender = null;
		for (Sender senderAux : senders) {
			if (senderAux.id == message.sender) {
				sender = senderAux;
			}
		}
		
		ProposedMessage proposedMessage = new ProposedMessage(this.id, message.tag, priority); 
		System.out.println("Sending " + proposedMessage);
		sender.send(proposedMessage);	
	}
	
	public synchronized void thirdPhase(String tag) {
		Long maxPriority = 0L;
		for (Long value: messagesMap.get(tag)) {
			if (maxPriority < value) {
				maxPriority = value;
			}
		}
		
		synchronized(mutexTimestamp) {
			System.out.println("Set timestamp to max " + "from " + this.timestamp.get() + " and " + maxPriority + ": " + Math.max(this.timestamp.get(), maxPriority));			
			this.timestamp.set(Math.max(this.timestamp.get(), maxPriority));
		}
		
		FinalMessage message = new FinalMessage(this.id, maxPriority, tag); 
		updateFromMessage(message);
		System.out.println("Broadcasting " + message + " to nodes...");		
		for (Sender sender: senders) {
			sender.send(message);
		}
		
		messagesMap.remove(tag);
	}

	@Override
	public synchronized void receive(AbstractMessage message) {
		if (message instanceof ReviseMessage) {
			secondPhase((ReviseMessage)message);
		} else if (message instanceof ProposedMessage) {
			ProposedMessage proposedMessage = (ProposedMessage) message;
			ArrayList<Long> timestamps = messagesMap.get(proposedMessage.tag);
			
			if (timestamps == null) {
				timestamps = new ArrayList<Long>();
			}
			
			timestamps.add(proposedMessage.timestamp);
			System.out.println("Received " + proposedMessage);
			System.out.println("Update proposed timestamps for " + proposedMessage.tag + ": " + timestamps);
			messagesMap.put(proposedMessage.tag, timestamps);
			if (timestamps.size() == node.maxPeers) {
				thirdPhase(proposedMessage.tag);
			}
		} else if (message instanceof FinalMessage) {
			System.out.println("Received " + (FinalMessage)message);
			updateFromMessage(message);
		}
	}

	@Override
	public synchronized void broadcast(ArrayList<Sender> senders, String line) {
		synchronized (mutexTimestamp) {
			this.timestamp.set(this.timestamp.get() + 1);			
			firstPhase(new ReviseMessage(new Command(line), this.id, this.timestamp.get()), senders);
		}
	}

	@Override
	public synchronized void updateFromMessage(AbstractMessage message) {
		FinalMessage finalMessage = (FinalMessage) message;
		PriorityQueue<QueueEntry> queueTemp = new PriorityQueue<QueueEntry>(10, new TimestampComparator());
		System.out.println("Update from " + message.toString());
		
		synchronized(mutexQueue) {
			for (QueueEntry entry: queue) {
				if (entry.message.tag.compareTo(finalMessage.tag) == 0) {
					entry.deliverable = true;
					entry.timestamp = finalMessage.timestamp;
				}
				queueTemp.add(entry);
			}
		}

		if (!queueTemp.isEmpty()) {
			System.out.println("Trying to deliver remaining queue entries: " + queueTemp);
			while (!queueTemp.isEmpty() &&  queueTemp.peek().deliverable == true) {
				QueueEntry entry = queueTemp.poll();
				System.out.println("Delivered " + entry);
				deliver(entry.message);
						
				synchronized (mutexTimestamp) {
					System.out.println("Updated timestamp to max from " + this.timestamp.get() + " and " + entry.timestamp + " +1: " + (Math.max(this.timestamp.get(), entry.timestamp) + 1));
					this.timestamp.set(Math.max(this.timestamp.get(), entry.timestamp) + 1);
				}
				
				synchronized(mutexQueue) {
					queue.remove(entry);
				}
			}
		}
	}

	@Override
	public void deliver(AbstractMessage message) {
		ReviseMessage reviseMessage = (ReviseMessage) message;
		updateGUI(reviseMessage.command);		
	}
}
