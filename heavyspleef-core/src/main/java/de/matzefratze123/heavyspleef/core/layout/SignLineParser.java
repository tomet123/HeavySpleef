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
package de.matzefratze123.heavyspleef.core.layout;

import java.text.ParseException;
import java.util.List;

import com.google.common.collect.Lists;

import de.matzefratze123.heavyspleef.core.layout.CustomizableLine.IfStatementFragment;
import de.matzefratze123.heavyspleef.core.layout.CustomizableLine.LineFragment;
import de.matzefratze123.heavyspleef.core.layout.CustomizableLine.StringFragment;
import de.matzefratze123.heavyspleef.core.layout.CustomizableLine.VariableFragment;
import de.matzefratze123.heavyspleef.core.script.IfStatement;
import de.matzefratze123.heavyspleef.core.script.ParsePositionException;
import de.matzefratze123.heavyspleef.core.script.VariableHolder;
import de.matzefratze123.heavyspleef.core.script.parser.StatementParser;
import de.matzefratze123.heavyspleef.core.script.parser.VariableHolderParser;

public class SignLineParser {

	private static final char IF_STATEMENT_OPENING_CHAR = '{';
	private static final char IF_STATEMENT_CLOSING_CHAR = '}';
	private static final char VARIABLE_START_CHAR = '$';
	private static final char VARIABLE_CLOSE_CHAR = ']';
	
	private final String line;
	private int position;
	private State state;
	private List<LineFragment> fragments;
	
	public SignLineParser(String line) {
		this.line = line;
		this.state = State.READ_STRING;
	}
	
	public void parse() throws ParseException {
		fragments = Lists.newArrayList();
		final int length = line.length();
		
		String tmpString = "";
		
		for (position = 0; position < length; position++) {
			char c = line.charAt(position);
			boolean isLast = position == length - 1;
			
			switch (state) {
			case READ_STRING:
				if (c != IF_STATEMENT_OPENING_CHAR && c != VARIABLE_START_CHAR && !isLast) {
					tmpString += c;
				} else {
					if (isLast) {
						tmpString += c;
					}
					
					if (!tmpString.isEmpty()) {
						StringFragment fragment = new StringFragment(tmpString);
						fragments.add(fragment);
						tmpString = "";
					}
					
					if (c == IF_STATEMENT_OPENING_CHAR) {
						state = State.READ_IF_STATEMENT;
					} else if (c == VARIABLE_START_CHAR) {
						state = State.READ_VARIABLE;
						position--;
					} else {
						state = State.READ_STRING;
					}
				}
				break;
			case READ_VARIABLE:
				tmpString += c;
				
				if (c == VARIABLE_CLOSE_CHAR) {
					VariableHolderParser parser = new VariableHolderParser(tmpString);
					VariableHolder holder = parser.parse();
					
					VariableFragment fragment = new VariableFragment(holder);
					fragments.add(fragment);
					tmpString = "";
					
					state = State.READ_STRING;
				}
				break;
			case READ_IF_STATEMENT:
				if (c != IF_STATEMENT_CLOSING_CHAR) {
					if (isLast) {
						throw new ParsePositionException("If-Statement \"" + tmpString + "\" must be closed with '" + IF_STATEMENT_CLOSING_CHAR + "'", position);
					}
					
					tmpString += c;
				} else {
					StatementParser statementParser = new StatementParser(tmpString);
					IfStatement statement = statementParser.parse();
					IfStatementFragment fragment = new IfStatementFragment(statement);
					
					fragments.add(fragment);
					tmpString = "";
					state = State.READ_STRING;
				}
				break;
			default:
				break;
			}
		}
	}

	public List<LineFragment> getFragments() {
		return fragments;
	}
	
	private enum State {
		
		READ_STRING,
		READ_IF_STATEMENT, 
		READ_VARIABLE;
		
	}

}