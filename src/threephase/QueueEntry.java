package threephase;

public class QueueEntry {
	public Boolean deliverable;
	public ReviseMessage message;
	public long timestamp;
	
	public QueueEntry(ReviseMessage message, long timestamp) {
		this.message = message;
		deliverable = false;
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return message.toString() + " " + deliverable + " " + timestamp;
	}
	
	public boolean equals(QueueEntry entry) {
		return entry.message.tag.compareTo(message.tag) == 0;
	}
}
