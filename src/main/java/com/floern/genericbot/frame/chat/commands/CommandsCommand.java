/* 
 * Floern, dev@floern.com, 2017, MIT Licence 
 */
package com.floern.genericbot.frame.chat.commands;

import com.floern.genericbot.frame.chat.ChatManager;
import com.floern.genericbot.frame.chat.commands.categories.CommandCategory;
import com.floern.genericbot.frame.chat.commands.categories.MetaCommandCategory;
import com.floern.genericbot.frame.chat.commands.classes.Command;
import com.floern.genericbot.frame.utils.StringUtil;

import org.sobotics.chatexchange.chat.Message;
import org.sobotics.chatexchange.chat.Room;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandsCommand extends Command implements MetaCommandCategory {


	@Override
	public String[] getAliases() {
		return new String[] { "commands", "cmds" };
	}


	@Override
	public String getUsageDescription() {
		return "show available commands";
	}


	@Override
	protected String execute(ChatManager chatManager, Room chatroom, Message message, String[] args) {
		StringBuilder sb = new StringBuilder();

		boolean showAll = args.length >= 2 && ("all".equals(args[1]) || "full".equals(args[1]));
		final int labelWidth = showAll ? 30 : 16;
		final int maxAliases = 4;
		final String codeIndentation = "    ";
		final String itemIndentation = "   ";

		List<Command> commands = chatManager.getAllCommands().stream()
				.filter(command -> command.getUsageDescription() != null
					&& (showAll || command.isCommanderPrivileged(chatManager, chatroom, message.getUser())))
				.collect(Collectors.toList());

		// create command groups
		Map<String, List<Command>> commandGroups = new LinkedHashMap<>();
		commands.stream().map(CommandCategory::getCommandCategory).distinct()
				.forEach(category -> commandGroups.put(category, new LinkedList<>()));

		// allot commands to their categories
		commands.forEach(command -> commandGroups.get(command.getCommandCategory()).add(command));

		// print commands
		commandGroups.forEach((categoryName, commandsGroup) -> {
			sb.append(codeIndentation).append(categoryName).append("\n");
			commandsGroup.forEach(command -> {
				sb.append(codeIndentation).append(itemIndentation)
						.append(StringUtil.padRight(showAll
								? String.join("|", Arrays.asList(command.getAliases())
									.subList(0, Math.min(maxAliases, command.getAliases().length)))
								: command.getAliases()[0], labelWidth))
						.append(" - ")
						.append(command.getUsageDescription())
						.append('\n');
			});
		});

		return sb.toString();
	}

}
