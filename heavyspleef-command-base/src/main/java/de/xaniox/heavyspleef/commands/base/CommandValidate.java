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

public class CommandValidate {
	
	public static void notNull(Object o) throws CommandException {
		notNull(o, null);
	}
	
	public static void notNull(Object o, String message) throws CommandException {
		if (o == null) {
			throw new CommandException(message);
		}
	}
	
	public static void isTrue(boolean condition) throws CommandException {
		isTrue(condition, null);
	}
	
	public static void isTrue(boolean condition, String message) throws CommandException {
		if (!condition) {
			throw new CommandException(message);
		}
	}
	
}