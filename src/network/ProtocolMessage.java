package network;

import command.Command;

public abstract class ProtocolMessage extends AbstractMessage {
	private static final long serialVersionUID = 3L;
	public Command command;
	
	public ProtocolMessage(Command command, int sender) {
		super(sender);
		this.command = command;
	}
	
	public String toString()
	{
		return command.toString() + " from " + sender;
	}
}
