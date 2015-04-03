package abcast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.ws.handler.MessageContext;

import command.Command;
import network.AbstractMessage;
import network.ConsistencyProtocol;
import network.Sender;
import node.Node;
import cbcast.CBCASTMessage;
import cbcast.CBCASTProtocol;

public class ABCASTProtocol extends CBCASTProtocol {
	int tokenHolder;
	private ArrayList<QueueEntry> delayQueue;
	private ArrayList<Sender> senders;
	ArrayList<String> setOrderQueue;
    private final Object mutexTimestamp = new Object();
    private final Object mutexDelayQueue = new Object();
	
	public ABCASTProtocol(int id, int tokenHolder, Node node, ArrayList<Sender> senders) {
		super(id, node);
		this.tokenHolder = tokenHolder;	
		this.senders = senders;
		this.delayQueue = new ArrayList<QueueEntry>();
		this.setOrderQueue = new ArrayList<String>();
	}

	@Override
	public synchronized void receive(AbstractMessage abstractMessage) {
		if (abstractMessage instanceof ABCASTMessage) {
			ABCASTMessage abcastMessage = (ABCASTMessage) abstractMessage;
			System.out.println("Node " + id + " received " + abcastMessage.toString());
		} else {
			SetOrderMessage setorderMessage = (SetOrderMessage) abstractMessage;
			System.out.println("Node " + id + " received " + setorderMessage.toString());			
		}
		
		updateFromMessage(abstractMessage);		
	}

	@Override
	public synchronized void broadcast(ArrayList<Sender> senders, String line) {
		ABCASTMessage abcastMessage = new ABCASTMessage(new Command(line), timestamps, id);
		System.out.println("Broadcasting " + abcastMessage + " to nodes...");
		synchronized (mutexTimestamp) {
			this.timestamps.set(id, timestamps.get(id) + 1);
			System.out.println("Timestamps updated to " + this.timestamps);			
		}
		
		for (Sender sender: senders) {
			sender.send(abcastMessage);
		}
		
		updateFromMessage(abcastMessage);		
	}

	@Override
	public void deliver(AbstractMessage message) {
		ABCASTMessage abcastMessage = (ABCASTMessage) message;
		System.out.println("Message delivered: " + abcastMessage);
		updateGUI(abcastMessage.command);

		synchronized (mutexTimestamp) {
			updateTimeVector(abcastMessage.timestamps);
		}

		
		if (this.id == this.tokenHolder) {
			SetOrderMessage setOrderMessage;
			ArrayList<String> tags = new ArrayList<String>();
			tags.add(abcastMessage.tag);
			setOrderMessage = new SetOrderMessage(this.id, tags);
			System.out.println("Broadcasting " + setOrderMessage + " from token holder " + this.id + " to nodes...");			
			for (Sender sender: senders) {
				sender.send(setOrderMessage);
			}			
		}
	}
	
	@Override
	public synchronized void updateFromMessage(AbstractMessage abstractMessage) {
		System.out.println("Updating from " + abstractMessage);
		if (this.id == this.tokenHolder) {
			if (abstractMessage instanceof ABCASTMessage) {
				ABCASTMessage abcastMessage = (ABCASTMessage)abstractMessage;		
				if (needsToBeDelayed(abcastMessage)) {
					System.out.println("Message needs to be delayed");
					synchronized (mutexDelayQueue) {
						delayQueue.add(new QueueEntry((ABCASTMessage)abstractMessage, true));
						Collections.sort(delayQueue, new TimestampVectorComparator());						
					}
				} else {
					deliver(abcastMessage);
				}
			}
		} else {
			// We don't expect the tokenHolder to receive a SETORDER Message
			if (abstractMessage instanceof SetOrderMessage ){
				SetOrderMessage setOrderMessage = (SetOrderMessage) abstractMessage;
				setOrderQueue.add(setOrderMessage.tags.get(0));
			} else if (abstractMessage instanceof ABCASTMessage) {
				QueueEntry entry = new QueueEntry((ABCASTMessage)abstractMessage, false);
				synchronized (mutexDelayQueue) {
					delayQueue.add(entry);
					Collections.sort(delayQueue, new TimestampVectorComparator());											
				}				
			}
		}
		
		ArrayList<QueueEntry> temp = new ArrayList<QueueEntry>();				
		System.out.println("Trying to deliver messages from delay queue: " + delayQueue);
		
		if (this.tokenHolder != this.id) {
			boolean noDeliverableEntry = true;
			for (QueueEntry entry: delayQueue) {
				if (entry.deliverable == true) {
					noDeliverableEntry = false;
				}
			}
			
			if (noDeliverableEntry) {
				for (String str: setOrderQueue) {
					for (QueueEntry entry2: delayQueue) {
						if (entry2.message.tag.equals(str)) {
							ABCASTMessage abcastMessageTemp = (ABCASTMessage)(entry2.message);
							System.out.println("Message delivered: " + abcastMessageTemp);							
							temp.add(entry2);
							deliver(abcastMessageTemp);
						}
					}
				}
				
				for (QueueEntry entry2: temp) {
					if (delayQueue.contains(entry2)) {
						delayQueue.remove(entry2);
						setOrderQueue.remove(entry2.message.tag);
					}
				}
			}
		}
		boolean atLeastOneDelivered = true;
		temp = new ArrayList<QueueEntry>();				
		
		while(atLeastOneDelivered) {
			atLeastOneDelivered = false;
			ArrayList<QueueEntry> undeliv = new ArrayList<QueueEntry>();
			synchronized(mutexDelayQueue) {
				for (QueueEntry entry: delayQueue) {
					ABCASTMessage abcastMessageTemp = (ABCASTMessage)(entry.message);
					if (this.id == this.tokenHolder) {
						if (!needsToBeDelayed(abcastMessageTemp) && entry.deliverable) {
							//updateDelayQueue(entry);
							temp.add(entry);
							System.out.println("Message delivered: " + abcastMessageTemp);
							deliver(abcastMessageTemp);
							atLeastOneDelivered = true;
						}
					} else {
						if (entry.deliverable) {
							for (String str: setOrderQueue) {
								for (QueueEntry entry2: undeliv) {
									if (entry2.message.tag.equals(str)) {
										abcastMessageTemp = (ABCASTMessage)(entry2.message);
										System.out.println("Message delivered: " + abcastMessageTemp);
										temp.add(entry2);
										deliver(abcastMessageTemp);
										atLeastOneDelivered = true;										
									}
								}
							}
							
							for (QueueEntry entry2: temp) {
								if (undeliv.contains(entry2)) {
									undeliv.remove(entry2);
									setOrderQueue.remove(entry2.message.tag);
								}
							}
						} else {
							undeliv.add(entry);
						}
					}
				}
				
				for (QueueEntry entry: temp) {
					delayQueue.remove(entry);
					Collections.sort(delayQueue, new TimestampVectorComparator());					
				}
			}
		}
		
	}

}
