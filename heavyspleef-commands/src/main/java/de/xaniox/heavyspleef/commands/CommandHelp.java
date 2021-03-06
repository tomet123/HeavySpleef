/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2016 Matthias Werning
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.xaniox.heavyspleef.commands;

import com.google.common.collect.Lists;
import de.xaniox.heavyspleef.commands.base.*;
import de.xaniox.heavyspleef.core.HeavySpleef;
import de.xaniox.heavyspleef.core.Permissions;
import de.xaniox.heavyspleef.core.i18n.I18N;
import de.xaniox.heavyspleef.core.i18n.I18NManager;
import de.xaniox.heavyspleef.core.i18n.Messages;
import de.xaniox.heavyspleef.core.player.SpleefPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CommandHelp {
	
	private static final String BASE_COMMAND = "spleef";
	private static final CommandContainerComparator COMPARATOR = new CommandContainerComparator();
	private static final int RECORDS_PER_PAGE = 10;

	private final I18N i18n = I18NManager.getGlobal();
	
	@Command(name = "help", permission = Permissions.PERMISSION_HELP,
			descref = Messages.Help.Description.HELP,
			usage = "/spleef help")
	public void onHelpCommand(CommandContext context, HeavySpleef heavySpleef) throws CommandException {
		CommandSender sender = context.getSender();
		if (sender instanceof Player) {
			sender = heavySpleef.getSpleefPlayer(sender);
		}
		
		SpleefCommandManager manager = (SpleefCommandManager) heavySpleef.getCommandManager();
		CommandManagerService service = manager.getService();
		
		CommandContainer container = service.getCommand(BASE_COMMAND);
		List<CommandContainer> childs = Lists.newArrayList(container.getChildCommands());
		for (Iterator<CommandContainer> iterator = childs.iterator(); iterator.hasNext();) {
			CommandContainer child = iterator.next();
			String permission = child.getPermission();
			
			if (permission.isEmpty() || sender.hasPermission(child.getPermission())) {
				continue;
			}
			
			iterator.remove();
		}
		
		Collections.sort(childs, COMPARATOR);
		
		int maxPage = (int) Math.ceil(childs.size() / (double)RECORDS_PER_PAGE);
		int page = 0;
		if (context.argsLength() > 0) {
			try {
				page = Integer.parseInt(context.getString(0));
			} catch (NumberFormatException nfe) {}
			
			page = Math.max(page - 1, 0);
		}
		
		String header = i18n.getVarString(Messages.Command.HELP_HEADER)
				.setVariable("page", String.valueOf(page + 1))
				.setVariable("max-pages", String.valueOf(maxPage))
				.toString();
		
		if (sender instanceof SpleefPlayer) {
			((SpleefPlayer)sender).sendUnprefixedMessage(header);
		} else {
			sender.sendMessage(header);
		}
		
		for (int i = page * RECORDS_PER_PAGE; i < page * RECORDS_PER_PAGE + RECORDS_PER_PAGE; i++) {
			if (i >= childs.size()) {
				break;
			}
			
			CommandContainer child = childs.get(i);
			
			String desc = child.getDescription();
			if (desc.isEmpty() && !child.getDescriptionRef().isEmpty()) {
				String i18nReference = child.getI18NRef();
				I18N i18n = I18NManager.getGlobal();
				
				if (!i18nReference.isEmpty()) {
					i18n = heavySpleef.getI18NManager().getI18N(i18nReference);
				}
				
				desc = i18n.getString(child.getDescriptionRef());
			}
			
			String record = i18n.getVarString(Messages.Command.HELP_RECORD)
					.setVariable("command_fq", child.getFullyQualifiedName())
					.setVariable("command", child.getName())
					.setVariable("usage", child.getUsage())
					.setVariable("description", desc)
					.toString();
			
			if (sender instanceof SpleefPlayer) {
				SpleefPlayer player = (SpleefPlayer) sender;
				player.sendUnprefixedMessage(record);
			} else {
				sender.sendMessage(record);
			}
		}
	}
	
	public static class CommandContainerComparator implements Comparator<CommandContainer> {

		@Override
		public int compare(CommandContainer o1, CommandContainer o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}

}