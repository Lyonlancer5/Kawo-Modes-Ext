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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.event.ServerChatEvent;

/**
 * Command to interact with the sylvr.mn group chat
 * 
 * @author Lyonlancer5
 */
public final class CommandCTCS extends CommandBase {

	private static CommandCTCS instance;
	private volatile MCClient chatClient;
	private boolean isChatInMCMode = true;

	private CommandCTCS() {
	}

	public String getCommandName() {
		return "ctcs";
	}

	public String getCommandUsage(ICommandSender sender) {
		return "commands.ctcs.usage";
	}

	public void processCommand(ICommandSender sender, String[] args) {

		if (sender instanceof CommandBlockLogic) {
			throw new CommandException("commands.ctcs.noUsr");
		}

		if (args.length == 0) {
			throw new WrongUsageException(getCommandUsage(sender));
		}

		if ("join".equalsIgnoreCase(args[0])) {
			if (args.length >= 3) {
				String addr = args[1], prt = args[2], usn = null;
				int port = Integer.parseInt(prt);

				try {
					StringBuilder b = new StringBuilder();
					for (int i = 3; i < args.length; i++)
						b.append(args[i] + " ");
					usn = b.toString().trim();
				} catch (Exception e) {
					usn = null;
				}

				chatClient = MCClient.create(addr, port, sender);
				if (usn != null && !usn.isEmpty())
					chatClient.setUserDefinedNick(usn);
				chatClient.start();
				isChatInMCMode = false;
			} else {
				throw new WrongUsageException("commands.ctcs.usage.join");
			}
			return;
		}

		if ("leave".equalsIgnoreCase(args[0])) {
			if (chatClient != null && chatClient.isConnected()) {
				if (args.length >= 2) {
					StringBuilder sb = new StringBuilder();
					for (int x = 1; x < args.length; x++) {
						sb.append(args[x] + " ");
					}
					chatClient.disconnect(sb.toString().trim());
				} else {
					chatClient.disconnect(null);
				}
			} else {
				throw new CommandException("commands.ctcs.notConnected");
			}
			return;
		}

		if ("nick".equalsIgnoreCase(args[0])) {
			if (chatClient != null && chatClient.isConnected()) {
				if (args.length >= 2) {
					StringBuilder sb = new StringBuilder();
					for (int x = 1; x < args.length; x++) {
						sb.append(args[x] + " ");
					}
					try {
						chatClient.sendMessage("/nick " + sb.toString().trim());
					} catch (IOException e) {
						throw new CommandException("commands.ctcs.nick.setFailed");
					}
				} else {
					try {
						chatClient.sendMessage("/nick");
					} catch (IOException e) {
						throw new CommandException("commands.ctcs.nick.setFailed");
					}
				}
			} else {
				throw new CommandException("commands.ctcs.notConnected");
			}
			return;
		}

		if ("switch".equalsIgnoreCase(args[0])) {
			if (chatClient != null && chatClient.isConnected()) {
				isChatInMCMode = !isChatInMCMode;
				if (isChatInMCMode) {
					sender.addChatMessage(new ChatComponentTranslation("commands.ctcs.switch.mc"));
				} else {
					sender.addChatMessage(new ChatComponentTranslation("commands.ctcs.switch.ctcs"));
				}
			} else {
				throw new CommandException("commands.ctcs.notConnected");
			}

			return;
		}

		throw new WrongUsageException("commands.ctcs.scmd");
	}

	public List<String> getCommandAliases() {
		return Arrays.asList("ctcs");
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	public int getRequiredPermissionLevel() {
		return 0;
	}

	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			return getListOfStringsMatchingLastWord(args, "join", "leave", "nick", "switch");
		}

		return null;
	}

	/**
	 * Gets the client, may be null if not yet connected
	 */
	public MCClient getChatClient() {
		return chatClient;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void handle(ServerChatEvent event) {

		if (chatClient != null && chatClient.isConnected() && !isChatInMCMode) {
			try {
				chatClient.sendMessage(event.message);
			} catch (IOException e) {
				event.player.addChatMessage(new ChatComponentTranslation("commands.ctcs.send.failed"));
			}
			event.setCanceled(true);
			return;
		}

		if (isChatInMCMode) {
			try {
				Field fl = ServerChatEvent.class.getDeclaredField("message");
				setModifier(ServerChatEvent.class.getDeclaredField("message"), Modifier.FINAL, false);
				fl.set(event, event.message.replace('`', '\u00A7'));
			} catch (Exception e) {
			}
		}
	}

	public static CommandCTCS getInstance() {
		if (instance == null)
			instance = new CommandCTCS();
		return instance;
	}
	
	private static Field fField_modifiers;

	/**
	 * Adds the modifiers {@code mod} to the given {@link Field} {@code field}
	 * if {@code flag} is true; removing them otherwise.
	 * 
	 * @param field
	 *            The field object
	 * @param mod
	 *            The modifiers
	 * @param flag
	 *            Flag to add or remove said modifiers
	 */
	private static Field setModifier(Field field, int mod, boolean flag) {
		if (fField_modifiers == null) {
			try {
				fField_modifiers = Field.class.getDeclaredField("modifiers");
				fField_modifiers.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Field modifier field error", e);
			}
		}

		try {
			field.setAccessible(true);
			int modifiers = fField_modifiers.getInt(field);
			if (flag) {
				modifiers |= mod;
			} else {
				modifiers &= ~mod;
			}
			fField_modifiers.setInt(field, modifiers);
			return field;
		} catch (Exception ex) {
			throw new RuntimeException("Could not set modifiers for field " + field.getName(), ex);
		}
	}


}
