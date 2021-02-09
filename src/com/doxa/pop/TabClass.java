package com.doxa.pop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class TabClass implements TabCompleter {
	
	Main plugin;
	public TabClass(Main main) {
		this.plugin = main;
	}

	List<String> arguments = new ArrayList<String>();
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(plugin.getPermission()));
			if (arguments.isEmpty()) {
				arguments.add("help"); arguments.add("toggle"); arguments.add("version");
			}
			
			List<String> result = new ArrayList<String>();
			if (args.length == 1) {
				for (String a : arguments) {
					if (a.toLowerCase().startsWith(args[0].toLowerCase()))
						result.add(a);
				}
				return result;
			}
		}
		return arguments;
		}

}