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
package de.matzefratze123.heavyspleef.core.event;

import de.matzefratze123.heavyspleef.core.game.Game;
import de.matzefratze123.heavyspleef.core.player.SpleefPlayer;

public class PlayerJoinGameEvent extends PlayerGameEvent {

	private boolean startGame;
	
	public PlayerJoinGameEvent(Game game, SpleefPlayer player) {
		super(game, player);
	}
	
	public boolean getStartGame() {
		return startGame;
	}
	
	public void setStartGame(boolean startGame) {
		this.startGame = startGame;
	}

}
