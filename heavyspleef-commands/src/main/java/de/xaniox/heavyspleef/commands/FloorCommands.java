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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.world.World;
import de.xaniox.heavyspleef.commands.base.*;
import de.xaniox.heavyspleef.core.HeavySpleef;
import de.xaniox.heavyspleef.core.Permissions;
import de.xaniox.heavyspleef.core.RegionVisualizer;
import de.xaniox.heavyspleef.core.floor.Floor;
import de.xaniox.heavyspleef.core.floor.SimpleClipboardFloor;
import de.xaniox.heavyspleef.core.game.Game;
import de.xaniox.heavyspleef.core.game.GameManager;
import de.xaniox.heavyspleef.core.hook.HookReference;
import de.xaniox.heavyspleef.core.i18n.I18N;
import de.xaniox.heavyspleef.core.i18n.I18NManager;
import de.xaniox.heavyspleef.core.i18n.Messages;
import de.xaniox.heavyspleef.core.player.SpleefPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FloorCommands {
	
	private final I18N i18n = I18NManager.getGlobal();
	
	@Command(name = "addfloor", permission = Permissions.PERMISSION_ADD_FLOOR, minArgs = 1,
			descref = Messages.Help.Description.ADDFLOOR,
			usage = "/spleef addfloor <Game> [Name]")
	@PlayerOnly
	public void onCommandAddFloor(CommandContext context, HeavySpleef heavySpleef) throws CommandException {
		SpleefPlayer player = heavySpleef.getSpleefPlayer(context.getSender());
		
		String gameName = context.getString(0);
		GameManager manager = heavySpleef.getGameManager();
		
		CommandValidate.isTrue(manager.hasGame(gameName), i18n.getVarString(Messages.Command.GAME_DOESNT_EXIST)
				.setVariable("game", gameName)
				.toString());
		Game game = manager.getGame(gameName);
		
		WorldEditPlugin plugin = (WorldEditPlugin) heavySpleef.getHookManager().getHook(HookReference.WORLDEDIT).getPlugin();
		com.sk89q.worldedit.entity.Player bukkitPlayer = plugin.wrapPlayer(player.getBukkitPlayer());
		World world = new BukkitWorld(player.getBukkitPlayer().getWorld());
		
		LocalSession session = plugin.getWorldEdit().getSessionManager().get(bukkitPlayer);
		RegionSelector selector = session.getRegionSelector(world);
		
		Region region;
		
		try {
			region = selector.getRegion().clone();
		} catch (IncompleteRegionException e) {
			player.sendMessage(i18n.getString(Messages.Command.DEFINE_FULL_WORLDEDIT_REGION));
			return;
		}
		
		validateSelectedRegion(region);
		
		//Create a session for copying all blocks
		EditSession editSession = session.createEditSession(bukkitPlayer);
		
		Clipboard clipboard = new BlockArrayClipboard(region);
		ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
		
		try {
			Operations.completeLegacy(copy);
		} catch (MaxChangedBlocksException e) {
			//We do not edit any blocks...
			e.printStackTrace();
			return;
		}
		
		String floorName = context.argsLength() > 1 ? context.getString(1) : generateUniqueFloorName(game);
		Floor floor = new SimpleClipboardFloor(floorName, clipboard);
		
		game.addFloor(floor);
		player.sendMessage(i18n.getVarString(Messages.Command.FLOOR_ADDED)
				.setVariable("floorname", floorName)
				.toString());
		
		//Save the game
		heavySpleef.getDatabaseHandler().saveGame(game, null);
	}
	
	@TabComplete("addfloor")
	public void onAddFloorTabComplete(CommandContext context, List<String> list, HeavySpleef heavySpleef) throws CommandException {
		GameManager manager = heavySpleef.getGameManager();
		
		if (context.argsLength() == 1) {
			for (Game game : manager.getGames()) {
				list.add(game.getName());
			}
		}
	}

	private void validateSelectedRegion(Region region) throws CommandException {
		if (!(region instanceof CuboidRegion) && !(region instanceof Polygonal2DRegion) && !(region instanceof CylinderRegion)) {
			throw new CommandException(i18n.getString(Messages.Command.WORLDEDIT_SELECTION_NOT_SUPPORTED));
		}
	}
	
	private String generateUniqueFloorName(Game game) {
		final String prefix = "floor_";
		int counter = 0;
		
		while (game.isFloorPresent(prefix + counter)) {
			++counter;
		}
		
		return prefix + counter;
	}
	
	@Command(name = "removefloor", permission = Permissions.PERMISSION_REMOVE_FLOOR, minArgs = 2,
			descref = Messages.Help.Description.REMOVEFLOOR,
			usage = "/spleef removefloor <Game> <Floorname>")
	public void onCommandRemoveFloor(CommandContext context, HeavySpleef heavySpleef) throws CommandException {
		CommandSender sender = context.getSender();
		if (sender instanceof Player) {
			sender = heavySpleef.getSpleefPlayer(sender);
		}
		
		String gameName = context.getString(0);
		GameManager manager = heavySpleef.getGameManager();
		
		CommandValidate.isTrue(manager.hasGame(gameName), i18n.getVarString(Messages.Command.GAME_DOESNT_EXIST)
				.setVariable("game", gameName)
				.toString());
		Game game = manager.getGame(gameName);
		
		String floorName = context.getString(1);
		
		CommandValidate.isTrue(game.isFloorPresent(floorName), i18n.getVarString(Messages.Command.FLOOR_NOT_PRESENT)
				.setVariable("floorname", floorName)
				.toString());
		
		Floor floor = game.removeFloor(floorName);
		sender.sendMessage(i18n.getVarString(Messages.Command.FLOOR_REMOVED)
				.setVariable("floorname", floor.getName())
				.toString());
		
		heavySpleef.getDatabaseHandler().saveGame(game, null);
	}
	
	@TabComplete("removefloor")
	public void onRemoveFloorTabComplete(CommandContext context, List<String> list, HeavySpleef heavySpleef) throws CommandException {
		GameManager manager = heavySpleef.getGameManager();
		
		if (context.argsLength() == 1) {
			for (Game game : manager.getGames()) {
				list.add(game.getName());
			}
		} else if (context.argsLength() == 2) {
			Game game = manager.getGame(context.getString(0));
			for (Floor floor : game.getFloors()) {
				list.add(floor.getName());
			}
		}
	}
	
	@Command(name = "showfloor", permission = Permissions.PERMISSION_SHOW_FLOOR, minArgs = 2,
			descref = Messages.Help.Description.SHOWFLOOR,
			usage = "/spleef showfloor <Game> <Floorname>")
	@PlayerOnly
	public void onCommandShowFloor(CommandContext context, HeavySpleef heavySpleef) throws CommandException {
		SpleefPlayer player = heavySpleef.getSpleefPlayer(context.getSender());
		
		String gameName = context.getString(0);
		GameManager manager = heavySpleef.getGameManager();
		
		CommandValidate.isTrue(manager.hasGame(gameName), i18n.getVarString(Messages.Command.GAME_DOESNT_EXIST)
				.setVariable("game", gameName)
				.toString());
		Game game = manager.getGame(gameName);
		
		String floorName = context.getString(1);
		
		CommandValidate.isTrue(game.isFloorPresent(floorName), i18n.getVarString(Messages.Command.FLOOR_NOT_PRESENT)
				.setVariable("floorname", floorName)
				.toString());
		
		Floor floor = game.getFloor(floorName);
		RegionVisualizer visualizer = heavySpleef.getRegionVisualizer();
		
		visualizer.visualize(floor.getRegion(), player, game.getWorld());
		player.sendMessage(i18n.getString(Messages.Command.REGION_VISUALIZED));
	}
	
	@TabComplete("showfloor")
	public void onShowFloorTabComplete(CommandContext context, List<String> list, HeavySpleef heavySpleef) throws CommandException {
		GameManager manager = heavySpleef.getGameManager();
		
		if (context.argsLength() == 1) {
			for (Game game : manager.getGames()) {
				list.add(game.getName());
			}
		} else if (context.argsLength() == 2) {
			Game game = manager.getGame(context.getString(0));
			for (Floor floor : game.getFloors()) {
				list.add(floor.getName());
			}
		}
	}

}