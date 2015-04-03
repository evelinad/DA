package network;

import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
	private static final long serialVersionUID = 2L;
	public Integer sender;
	
	public AbstractMessage(int sender) {
		this.sender = sender;
	}
	
	public abstract String toString();
}
