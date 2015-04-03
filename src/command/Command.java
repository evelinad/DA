package command;

import java.io.Serializable;

public class Command implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String INSERT = "ins";
	public static final String DELETE = "del"; 
	
	public Character c;
	public Integer pos;
	public String type;
	
	public Command(String command) {
		command = command.replace("(", "!").replace(")", "").replace(",", "!");
		String[] tokens = command.split("!");
		type = tokens[0];
		if (type.compareTo(INSERT) == 0) {
			c = tokens[1].charAt(1);
			pos = Integer.parseInt(tokens[2]);
		} else {
			pos = Integer.parseInt(tokens[1]);
		}
	}
	public String toString()
	{
		return type + " " + c + " " + pos;
	}
}
