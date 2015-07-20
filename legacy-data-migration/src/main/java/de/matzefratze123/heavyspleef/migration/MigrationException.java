/*
 * This file is part of HeavySpleef.
 * Copyright (c) 2014-2015 matzefratze123
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
package de.matzefratze123.heavyspleef.migration;

public class MigrationException extends Exception {

	private static final long serialVersionUID = 4066992279037576527L;
	
	public MigrationException() {}
	
	public MigrationException(String message) {
		super(message);
	}
	
	public MigrationException(Throwable cause) {
		super(cause);
	}
	
	public MigrationException(String message, Throwable cause) {
		super(message, cause);
	}

}
