package threephase;

import command.Command;
import network.ProtocolMessage;

public class ReviseMessage extends ProtocolMessage{
	private static final long serialVersionUID = 4L;
	String tag;
	Long timestamp;
	
	public ReviseMessage(Command command, int sender, long timestamp) {
		super(command, sender);
		this.timestamp = timestamp;
		this.tag =
			String.valueOf(this.command.hashCode()) +
			String.valueOf(this.sender) +
			String.valueOf(this.timestamp); 	
	}

	@Override
	public String toString()
	{
		return "REVISE message " + " tag " + tag  + " timestamp " + timestamp + " command " + command.toString() + " from " + sender;
	}
}
