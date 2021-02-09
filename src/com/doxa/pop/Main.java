package com.doxa.pop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
	
	//PLUGIN VERSION
	private double version = 1.2;
	
	//CLASSES
	public Popper popper;
	public TabClass tabclass;
	
	private int launch_amt_y;
	public int getLaunchAmount() {
		return launch_amt_y;
	}
	
	private int launch_amt_vert;
	public int getLaunchAmountVert() {
		return launch_amt_vert;
	}
	
	private int cooldown_amt;
	public int getCooldownAmount() {
		return cooldown_amt;
	}

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		launch_amt_y = getConfig().getInt("pop-height-amount");
		launch_amt_vert = getConfig().getInt("pop-vertical-amount");
		cooldown_amt = getConfig().getInt("cooldown");
		char cc = '&';
		setPrefix(ChatColor.translateAlternateColorCodes(cc, this.getConfig().getString("prefix")) + " ");
		this.popper = new Popper(this);
		this.tabclass = new TabClass(this);
		this.getCommand("popper").setTabCompleter(tabclass);
		this.getServer().getPluginManager().registerEvents(popper, this);
		for (Player p : Bukkit.getOnlinePlayers()) {
			try {
				if (getConfig().getBoolean("toggle." + p.getName())) {
					return;
				}
			} catch(NullPointerException e) {
				getConfig().set("toggle." + p.getName(), true);
				saveConfig();
			}
		}
	}
	
	
	@Override
	public void onDisable() {
		System.out.println("Disengaging Popper v" + version + "!");
	}
	
	private String perm = "popper.use";
	public String getPermission() {
		return perm;
	}
	
	public double getVersion() {
		return version;
	}
	
	private String prefix;
	private void setPrefix(String p) {
		prefix = p;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public boolean isPlayerToggledOn(Player player) {
		boolean t = getConfig().getBoolean("toggle." + player.getName());
		return t;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (label.equalsIgnoreCase("popper")) {
				if (args.length == 1) {
					if (player.hasPermission(perm)) {
						boolean player_toggle = getConfig().getBoolean("toggle." + player.getName());
						switch (args[0].toLowerCase()) {
							case "help":
								player.sendMessage(prefix + "\n");
								player.sendMessage("");
								player.sendMessage(ChatColor.LIGHT_PURPLE + 
										"To use PlayerPopper: Sneak and RightClick a player!");
								player.sendMessage(ChatColor.DARK_PURPLE + 
										"/Popper toggle " + ChatColor.LIGHT_PURPLE + "- toggles on/off your player popper!");
								player.sendMessage("");
								break;
							case "toggle":
								if (player_toggle == true) {
									getConfig().set("toggle." + player.getName(), false);
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
											new TextComponent(ChatColor.RED + "PlayerPopper toggled off!"));
								} else {
									getConfig().set("toggle." + player.getName(), true);
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
											new TextComponent(ChatColor.GREEN + "PlayerPopper toggled on!"));
								}
								saveConfig();
								break;
							case "version":
								player.sendTitle(ChatColor.AQUA + "Version: " + version, 
										ChatColor.LIGHT_PURPLE + "Created by Nick Doxa", 1, 60, 1);
								break;
							default:
								player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: try /popper help!");
								break;
						}
					} else {
						player.sendMessage(prefix + ChatColor.RED +  "Insufficient permissions!");
					}
				}
			} else {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: try /popper help!");
			}
		} else {
			System.out.println("Silly Console, Poppings for kids!");
		}
		return false;
	}
	
}
