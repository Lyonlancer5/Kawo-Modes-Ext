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
package net.lyonlancer5.mcmp.uuem.my;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.lang3.Validate;

import net.lyonlancer5.mcmp.uuem.modes.ByteMesser;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * <pre>
 * 2123-ANTA_NO_TSUGINO-2319 EXTERNAL CHAT SYSTEM
 * - PROTOCOL VERSION: 1.1
 * 
 * Client for the sYlvr.mn group chat
 * </pre>
 * 
 * @author Lyonlancer5
 * @version 1.1.4-mc
 */
public class MCClient {

	private static MCClient previousInstance = null;

	private final String server;
	private final int port;
	private final ICommandSender owner;

	private BufferedInputStream sInput;
	private BufferedOutputStream sOutput;
	private Socket socket;
	private boolean broadcastMode;
	private ServerListener listener;
	private String username;

	public static MCClient create(String inetAddress, int portNumber, ICommandSender sender) {
		if (previousInstance != null) {
			previousInstance.disconnect("Connecting to new server");
			previousInstance = null;
		}
		MCClient cl = new MCClient(inetAddress, portNumber, sender);
		previousInstance = cl;
		return cl;
	}

	private MCClient(String inetAddress, int portNumber, ICommandSender sender) {
		server = inetAddress;
		port = portNumber;
		owner = Validate.notNull(sender, "Sender must not be null");
		if (sender instanceof MinecraftServer) {
			username = "Server" + Integer.toUnsignedString(sender.hashCode(), 10);
			broadcastMode = true;
		} else {
			username = sender.getCommandSenderName();
			broadcastMode = false;
		}
	}

	public boolean start() {
		try {
			socket = new Socket(server, port);
		} catch (Exception e) {
			display("commands.ctcs.server.connectFailed", e.getMessage());
			return false;
		}

		display("commands.ctcs.server.connected", socket.getInetAddress().getHostName(), socket.getPort());

		try {
			// expect 4KB per transfer
			sInput = new BufferedInputStream(socket.getInputStream(), 4096);
			sOutput = new BufferedOutputStream(socket.getOutputStream(), 4096);
		} catch (IOException e) {
			display("commands.ctcs.server.noEst");
			return false;
		}

		listener = new ServerListener();
		listener.start();

		try {
			sendMessage(System.getProperty("user.name"));
			sendMessage(username);
		} catch (IOException e) {
			display("commands.ctcs.server.noLogin", e.getMessage());
			disconnect("login error");
			return false;
		}
		return true;
	}

	private void display(final String msg, Object... args) {
		ChatComponentTranslation text = new ChatComponentTranslation(msg.replaceAll("`", "\u00A7"), args);

		if (broadcastMode && owner instanceof MinecraftServer) {
			MinecraftServer server = (MinecraftServer) owner;
			for (Object obj : server.getConfigurationManager().playerEntityList) {
				EntityPlayerMP player = (EntityPlayerMP) obj;
				player.addChatMessage(text);
			}
		}

		owner.addChatMessage(text);
	}

	public void sendMessage(String msg) throws IOException {
		if (msg != null && !msg.isEmpty()) {
			msg = msg.trim();

			if (msg.equalsIgnoreCase("/PING")) {
				msg = msg + " " + System.currentTimeMillis();
			}

			byte[] n = ByteMesser.SHIFT_OFFSET.generate(ByteMesser.NEGATE.generate(msg.getBytes("UTF-8")));
			writeShort(n.length);
			sOutput.write(n, 0, n.length);
			sOutput.flush();
		}
	}

	public void disconnect(String msg) {
		try {
			if (msg != null && !msg.isEmpty()) {
				sendMessage("/quit " + msg);
			} else {
				sendMessage("/quit");
			}
		} catch (IOException e1) {
		}

		listener.dorun = false;

		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
		}

		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
		}

		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
	}

	private class ServerListener extends Thread {

		private volatile boolean dorun = true;

		private ServerListener() {
			setName("XC's Chat Client");
			setDaemon(true);
		}

		public void run() {
			while (true) {
				try {
					short len = readShort();
					if (!dorun || len < 0)
						break;
					byte[] n = new byte[len];
					sInput.read(n);

					String msg = new String(ByteMesser.NEGATE.generate(ByteMesser.SHIFT_REVERSE.generate(n)), "UTF-8");

					if (msg.startsWith("/PONG") || msg.startsWith("/pong")) {
						try {
							long l = System.currentTimeMillis();
							String[] val = msg.split(" ", 2);
							long m = Long.parseLong(val[1]);
							display("PING reply: " + (l - m) + "ms");
						} catch (Exception e) {
							display("commands.ctcs.server.invPingReply");
						}

					} else {
						display(msg);
					}

				} catch (IOException e) {
					break;
				}
			}
			display("commands.ctcs.server.leave");
		}
	}

	private short readShort() throws IOException {
		int v1 = sInput.read();
		int v2 = sInput.read();

		if ((v1 | v2) == 0) {
			throw new EOFException();
		}
		return (short) ((v1 << 8) + (v2 << 0));
	}

	private void writeShort(int val) throws IOException {
		sOutput.write((val >>> 8) & 0xFF);
		sOutput.write((val >>> 0) & 0xFF);
	}

	public boolean isConnected() {
		try {
			return listener.dorun;
		} catch (Exception e) {
			return false;
		}
	}

	public void setUserDefinedNick(String val) {
		username = Validate.notBlank(val);
	}
}
