package abcast;

public class QueueEntry {
	ABCASTMessage message;
	Boolean deliverable;
	
	public QueueEntry(ABCASTMessage message, boolean deliverable) {
		this.message = message;
		this.deliverable = deliverable;
	}
	
	
	public String toString() {
		return  message.toString() + " " + deliverable;
	}
	
	public boolean equals(QueueEntry entry) {
		return entry.message.tag.compareTo(message.tag) == 0;
	}
}
