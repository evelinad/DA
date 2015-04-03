package abcast;

import java.util.ArrayList;

import network.AbstractMessage;

public class SetOrderMessage extends AbstractMessage {
	public ArrayList<String> tags;
	
	public SetOrderMessage(int sender, ArrayList<String> tags) {
		super(sender);
		this.tags = tags;
	}
	
	public String toString() {
		return "SETORDER message with tags " + tags.toString() + " from " + sender;
	}
}
