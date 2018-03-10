package net.lyonlancer5.mcmp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Describes a file inside a directory
 */
public class Maiz implements Serializable {

	private static final long serialVersionUID = 8828661617260332485L;
	
	/**
	 * The filename of the file
	 */
	public final String filename;
	
	/**
	 * The length of the file (in bytes)
	 */
	public final long size;
	
	/**
	 * The last modified date since epoch
	 */
	public final long lastModified;
	
	/**
	 * Determines whether this file entry is a directory or not:
	 * 
	 * 1 if directory, -1 if file, 0 if unidentifiable
	 */
	public final int directoryIdentifier;
	
	public Maiz(String filename, long size, long lastModified, int isDirectory){
		this.filename = filename;
		this.size = size;
		this.lastModified = lastModified;
		this.directoryIdentifier = isDirectory;
	}
	
	public String toString(){
		return String.format("%s [Type: %s] [Size: %d bytes] [Date Modified: %s]",
				filename, (directoryIdentifier == 1 ? "Directory" : (directoryIdentifier == -1 ? "File" : "Unknown")), size,
				(new SimpleDateFormat("MMM-dd-YYYY HH:mm:ss").format(new Date(lastModified))));
	}
}
