package cbcast;

import java.util.ArrayList;

import command.Command;
import network.AbstractMessage;

public class CBCASTMessage extends AbstractMessage {
	public ArrayList<Long> timestamps;
	public Command command;
	
	public CBCASTMessage(Command command, ArrayList<Long> timestamps, Integer sender) {
		super(sender);
		this.command = command;
		this.timestamps = timestamps;
	}

	public String toString() {
		return "CBCAST message with " + command.toString() + " with " + timestamps.toString() + " from " + sender; 
	}
}
