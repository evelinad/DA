package abcast;

import java.util.ArrayList;

import network.AbstractMessage;
import command.Command;
import cbcast.CBCASTMessage;

public class ABCASTMessage extends CBCASTMessage {
	String tag;	
		
	public ABCASTMessage(Command command, ArrayList<Long> timestamps,
			Integer sender) {
		super(command, timestamps, sender);
		this.tag =
				Integer.toString(this.command.hashCode()) +
				String.valueOf(this.sender) +
				String.valueOf(this.timestamps.toString().hashCode());
	}

	@Override
	public String toString() {
		return "ABCAST message with command " + command.toString() + " with " + tag + " from " + this.sender ;
	}
}
