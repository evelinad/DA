package threephase;

import network.AbstractMessage;

public class FinalMessage extends AbstractMessage {
	private static final long serialVersionUID = 6L;
	String tag;
	Long timestamp;
	
	public FinalMessage(int sender, long timestamp, String tag) {
		super(sender);
		this.timestamp = timestamp;
		this.tag = tag;
	}
	
	public String toString()
	{
		return "FINAL message " + " tag " + tag  + " timestamp " + timestamp + " from " + sender;
	}

}
