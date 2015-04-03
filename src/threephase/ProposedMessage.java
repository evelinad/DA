package threephase;

import network.AbstractMessage;

public class ProposedMessage extends AbstractMessage{
	private static final long serialVersionUID = 5L;
	String tag;
	Long timestamp;
	
	public ProposedMessage(int sender, String tag, long timestamp) {
		super(sender);
		this.timestamp = timestamp;
		this.tag = tag;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "PROPOSED message " + tag + " timestamp " + timestamp + " from " + sender;
	}
	

}
