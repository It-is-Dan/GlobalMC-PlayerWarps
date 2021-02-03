package me.RaceAgainstTime.SimplySurvivalPlayerWarps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin {

	private Economy econ;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getConfig().options().copyDefaults(true);
		getLogger().info(ChatColor.GREEN + "SSPlayerWarps has been enabled!");
		if (!setupEconomy()) {
			this.getLogger().severe("Disabled due to no Vault dependency found!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info(ChatColor.GREEN + "SSPlayerWarps successfully hooked into Vault!");
		}
	}

	@Override
	public void onDisable() {
		getLogger().info(ChatColor.RED + "SSPlayerWarps has been disabled!");
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Start of Setting a Warp
		if (label.contentEquals("setpwarp")) {
			Player player = (Player) sender;
			if (player.hasPermission("pwarp.use")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "" + "Usage: /setpwarp <warp>");
				}
			}
			if (econ.has(player, 100000)) {
				EconomyResponse r = econ.withdrawPlayer(player, 100000);
				if (r.transactionSuccess()) {
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&7You have paid &e$100,000 &7to set a warp!"));

					this.getConfig().set("Warps." + player.getName() + "." + args[0] + ".coords" + ".X",
							player.getLocation().getX());
					this.getConfig().set("Warps." + player.getName() + "." + args[0] + ".coords" + ".Y",
							player.getLocation().getY());
					this.getConfig().set("Warps." + player.getName() + "." + args[0] + ".coords" + ".Z",
							player.getLocation().getZ());
					this.getConfig().set("Warps." + player.getName() + "." + args[0] + ".coords" + ".Yaw",
							player.getLocation().getYaw());
					this.getConfig().set("Warps." + player.getName() + "." + args[0] + ".coords" + ".Pitch",
							player.getLocation().getPitch());
					this.saveConfig();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Your warp has been set."));

				}
			} else {
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&7You need &e$100,000 &7to set a warp!"));
			}
			// End of Setting a Warp
		}
		// Start of Warping to a Warp
		if (label.equalsIgnoreCase("pwarp")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Available Commands:" + "\n" + "&e/pwarp <playername> <warpname>" + "\n" + "&e/pwarp list <playername>"));
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					sender.sendMessage(ChatColor.GREEN + "" + "Plugin Configuration Reloaded");
					this.reloadConfig();
					return true;
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Please enter &e" + args[0] + "'s&7 warp name!" ));
				}
			}
			if (args.length == 2) {
				if (this.getConfig().contains("Warps." + args[0] + "." + args[1])) {
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&7You have successfully teleported to&e " + args[0] + "&7's warp!"));

					double locationX = this.getConfig()
							.getDouble("Warps." + args[0] + "." + args[1] + ".coords" + ".X");
					double locationY = this.getConfig()
							.getDouble("Warps." + args[0] + "." + args[1] + ".coords" + ".Y");
					double locationZ = this.getConfig()
							.getDouble("Warps." + args[0] + "." + args[1] + ".coords" + ".Z");
					float pitch = this.getConfig().getInt("Warps." + args[0] + "." + args[1] + ".coords" + ".Pitch");
					float yaw = this.getConfig().getInt("Warps." + args[0] + "." + args[1] + ".coords" + ".Yaw");
					World world = Bukkit.getServer().getWorld("world");
					Player player = (Player) sender;
					player.teleport(new Location(world, locationX, locationY, locationZ, yaw, pitch));
					
					
				} else {
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&7Could not find a warp named &e" + args[1]));
				}
				if (args[0].equalsIgnoreCase("list")) {
					if (this.getConfig().contains("Warps." + args[1])) {
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', "&e" + args[1] + "&7's current warps:"));
						for (String key : this.getConfig().getConfigurationSection("Warps." + args[1]).getKeys(false)) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + key));
						}
					}
					return true;
				}

			}
			// End of Warping to a Warp
		}
		// Start of Deleting a Warp
		if (label.contentEquals("delpwarp")) {
			Player player = (Player) sender;
			if (label.contentEquals("delpwarp")) {
				if (args.length > 0) {
					if (this.getConfig().contains("Warps." + player.getName() + "." + args[0])) {

						this.getConfig().set("Warps." + player.getName() + "." + args[0], null);
						this.saveConfig();
						this.reloadConfig();
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&7Warp &e" + args[0] + " &7has been deleted!"));

					}
				}
			}
			// End of Deleting a Warp
		}

		// end of command block
		return true;
	}
}
