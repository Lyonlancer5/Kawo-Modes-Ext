package net.lyonlancer5.mcmp;

import java.io.Serializable;

/**
 * Object to transfer message between servers and clients
 * @author Sara Hachimitsu (inviderti.101)
 * @since b1.0.2
 */
public class Kaos implements Serializable {
	
	private static final long serialVersionUID = -2003939550559451648L;
	
	/**
	 * The sender's username
	 */
	public final String source;
	
	/**
	 * The receiver's username
	 */
	public final String destination;
	
	/**
	 * The string containing keywords on what was sent
	 */
	public final String message;
	
	/**
	 * The array containing the data sent by the local client
	 */
	public final Object[] contents;
	
	public Kaos(String sender, String destination, String message, Object... contents) {
		this.source = sender;
		this.destination = destination;
		this.message = message;
		this.contents = contents;
	}
	
	public String toString(){
		return String.format("MessageObject [Owner: %s] [Destination: %s] [Keyword: %s] [Content Length: %d]",
				source, destination, message, contents.length);
	}
}
