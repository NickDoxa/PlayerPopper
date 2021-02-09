package com.doxa.pop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;


public class Popper implements Listener {
	
	Main plugin;
	public Popper(Main main) {
		this.plugin = main;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPlayedBefore()) {
			try {
				if (!plugin.getConfig().getBoolean("toggle." + player.getName())) {
					plugin.getConfig().set("toggle." + player.getName(), false);
					plugin.saveConfig();
					return;
				}
				if (plugin.getConfig().getBoolean("toggle." + player.getName())) {
					plugin.getConfig().set("toggle." + player.getName(), true);
					plugin.saveConfig();
					return;
				}
			} catch(NullPointerException e) {
				plugin.getConfig().set("toggle." + player.getName(), true);
				plugin.saveConfig();
			}
		} else {
			plugin.getConfig().set("toggle." + player.getName(), true);
			plugin.saveConfig();
		}
	}
	
	Map<String, Long> pop_cd = new HashMap<String, Long>();
	public void popPlayer(Player sender, Player player, int amt_y, int amt_vert) {
		Vector vec = sender.getEyeLocation().getDirection();
		player.setVelocity(new Vector((vec.getBlockX() * amt_vert), vec.getBlockY() + amt_y, (vec.getBlockZ() * amt_vert)));
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.LIGHT_PURPLE + 
				"You have been popped by " + ChatColor.BOLD + sender.getName()));
	}

	boolean msg = false;
	boolean cd_msg = false;
	@EventHandler
	public void onClick(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (event.getRightClicked() instanceof Player) {
			Player player_clicked = (Player) event.getRightClicked();
			if (plugin.isPlayerToggledOn(player) && player.isSneaking())
			if (plugin.isPlayerToggledOn(player_clicked)) {
				if (player.hasPermission(plugin.getPermission())) {
					if (pop_cd.containsKey(player.getName())) {
						if (pop_cd.get(player.getName()) > System.currentTimeMillis()) {
							long timeleft = (pop_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
							if (cd_msg == false) {
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
									new TextComponent(ChatColor.RED + "Cannot use popper for " + timeleft + " seconds"));
							}
							return;
							
						}
					}
					pop_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldownAmount() * 1000));
					cd_msg = true;
					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run() {
			            	cd_msg = false;
			            }
			        }, (2 * 20));
					popPlayer(player, player_clicked, plugin.getLaunchAmount(), plugin.getLaunchAmountVert());
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.LIGHT_PURPLE + "You popped " 
					+ ChatColor.BOLD + player_clicked.getName()));
				} else {
					if (msg) {
						return;
					} else {
					msg = true;	
					player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Insufficient Permissions");
					}
					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run() {
			            	msg = false;
			            }
			        }, (2 * 20));
				}
			} else {
				if (msg) {
					return;
				} else {
				msg = true;	
				player.sendMessage(plugin.getPrefix() + 
						ChatColor.RED + "The player you clicked has popping toggled off!");
				}
				BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run() {
		            	msg = false;
		            }
		        }, (2 * 20));
			}
		}
	}
	
}
