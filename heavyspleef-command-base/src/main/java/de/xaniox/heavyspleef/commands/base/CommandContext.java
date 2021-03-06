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
package de.xaniox.heavyspleef.commands.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandContext {
	
	private String[] args;
	private CommandContainer command;
	private CommandSender sender;
	
	public CommandContext(String[] args, CommandContainer command, CommandSender sender) {
		this.args = args;
		this.command = command;
		this.sender = sender;
	}
	
	public String getString(int index) throws CommandException {
		checkBounds(index);
		
		return args[index];
	}
	
	public String getStringSafely(int index) {
		try {
			return getString(index);
		} catch (CommandException e) {
			return "";
		}
	}
	
	public Integer getInt(int index) throws CommandException {
		checkBounds(index);
		
		return CommandManagerService.getTransformer(Integer.class).transform(args[index]);
	}
	
	public Double getDouble(int index) throws CommandException {
		checkBounds(index);
		
		return CommandManagerService.getTransformer(Double.class).transform(args[index]);
	}
	
	public Boolean getBoolean(int index) throws CommandException {
		checkBounds(index);
		
		return CommandManagerService.getTransformer(Boolean.class).transform(args[index]);
	}
	
	public Player getPlayer(int index) throws CommandException {
		checkBounds(index);
		
		return CommandManagerService.getTransformer(Player.class).transform(args[index]);
	}
	
	public int argsLength() {
		return args.length;
	}
	
	private void checkBounds(int index) throws CommandException {
		if (index < 0 || index >= args.length) {
			throw new CommandException("args index out of bounds (0 <= index < " + args.length + ")");
		}
	}
	
	public <R> R getArgument(int index, Class<R> clazz) throws CommandException {
		checkBounds(index);
		
		Transformer<R> transformer = CommandManagerService.getTransformer(clazz);
		return transformer.transform(args[index]);
	}
	
	public String[] args() {
		return args;
	}
	
	public CommandContainer getCommand() {
		return command;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends CommandSender> T getSender() {
		return (T) sender;
	}
	
}