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
package de.matzefratze123.heavyspleef.core.stats;

import java.util.Set;
import java.util.UUID;

import de.matzefratze123.heavyspleef.core.game.Rateable;
import de.matzefratze123.heavyspleef.core.script.Variable;
import de.matzefratze123.heavyspleef.core.script.VariableSuppliable;
import lombok.Getter;
import lombok.Setter;

public class Statistic implements Comparable<Statistic>, Rateable, VariableSuppliable {

	private static final double START_RATING = 1000D;
	
	@Getter
	private UUID uniqueIdentifier;
	@Getter @Setter
	private String lastName;
	@Getter @Setter
	private int wins;
	@Getter @Setter
	private int losses;
	@Getter @Setter
	private int knockouts;
	@Getter @Setter
	private int gamesPlayed;
	@Getter @Setter
	private long timePlayed;
	@Getter @Setter
	private int blocksBroken;
	@Getter @Setter
	private double rating = START_RATING;
	
	public Statistic() {}
	
	public Statistic(UUID uuid) {
		this.uniqueIdentifier = uuid;
	}
	
	@Override
	public int compareTo(Statistic o) {
		return Double.valueOf(rating).compareTo(o.rating);
	}

	public boolean isEmpty() {
		return wins == 0 && losses == 0 && knockouts == 0 && gamesPlayed == 0 && timePlayed == 0 && blocksBroken == 0 && rating == START_RATING;
	}

	@Override
	public void supply(Set<Variable> vars, Set<String> requested) {
		vars.add(new Variable("last-name", lastName));
		vars.add(new Variable("wins", wins));
		vars.add(new Variable("losses", losses));
		vars.add(new Variable("knockouts", knockouts));
		vars.add(new Variable("games-played", gamesPlayed));
		vars.add(new Variable("time-played", timePlayed));
		vars.add(new Variable("blocks-broken", blocksBroken));
		vars.add(new Variable("rating", (int)rating));
		vars.add(new Variable("rating-exact", rating));
	}
	
}
