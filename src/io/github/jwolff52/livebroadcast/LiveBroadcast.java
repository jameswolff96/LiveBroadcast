package io.github.jwolff52.livebroadcast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class LiveBroadcast extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");

	public static LiveBroadcast plugin;

	public static SettingsManager sm;

	private static PluginDescriptionFile pdf;

	private String state, broadcastTitle;

	private boolean toggle = true;

	private int timer, configNumber = 1, maxMessages = 0;

	@Override
	public void onEnable() {
		pdf = getDescription();
		sm = SettingsManager.getInstance();
		sm.setup(this);

		if (!(new File(getDataFolder(), "README.md").exists())) {
			InputStream is = LiveBroadcast.class
					.getResourceAsStream("/README.md");
			OutputStream os;
			int readBytes;
			byte[] buffer = new byte[4096];
			try {
				os = new FileOutputStream(new File(getDataFolder()
						+ "/README.txt"));
				while ((readBytes = is.read(buffer)) > 0) {
					os.write(buffer, 0, readBytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		while (true) {
			if (sm.config.getString(configNumber + "") != null) {
				configNumber++;
				maxMessages++;
			} else {
				configNumber = 1;
				break;
			}
		}

		setBroadcastTitle(parseColors(sm.config.getString("title")));
		timer = getConfig().getInt("timer") * 20;
		/*************************************************************/
		// Scheduler that prints the messages every 'timer' seconds
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					@Override
					public void run() {
						if (sm.config.getString(configNumber + "") != null) {
							if (!sm.config.getString(configNumber + "")
									.equalsIgnoreCase("empty")) {
								if (getToggle())
									Bukkit.broadcastMessage(broadcastTitle
											+ parseColors(sm.config
													.getString(configNumber
															+ "")));
							}
							configNumber++;
						} else {
							configNumber = 1;
						}
					}
				}, 0L, timer);
		/*************************************************************/
		this.logger.info(pdf.getName() + " Version: " + pdf.getVersion()
				+ " has been enabled!");
	}

	@Override
	public void onDisable() {
		pdf = getDescription();
		this.logger.info(pdf.getName() + " has been Disabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (label.equalsIgnoreCase("lb")) {
				if (args[0].equalsIgnoreCase("toggle")) {
					if ((!(player.hasPermission("lb.toggle")))
							|| (!(player.hasPermission("lb.*")))) {
						return false;
					} else {
						toggle(player);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("credits")) {
					if ((!(player.hasPermission("lb.credits")))
							|| (!(player.hasPermission("lb.*")))) {
						return false;
					} else {
						credits(player);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("config")) {
					if (!(args.length < 2)) {
						if (player.hasPermission("lb.config.*")
								|| player.hasPermission("lb.*")) {
							if (args[1].equalsIgnoreCase("add")) {
								if (!(args.length < 3)) {
									add(player, args);
									return true;
								} else {
									player.sendMessage("Usage: /lb config add <message>");
									return false;
								}
							} else if (args[1].equalsIgnoreCase("del")) {
								if (!(args.length < 3)) {
									del(player, args);
									return true;
								} else {
									player.sendMessage("Usage: /lb config del <number>");
									return false;
								}
							} else if (args[1].equalsIgnoreCase("list")) {
								list(player);
								return true;
							}
						} else if (player.hasPermission("lb.config.add")) {
							if (args[1].equalsIgnoreCase("add")) {
								if (!(args.length < 3)) {
									add(player, args);
									return true;
								} else {
									player.sendMessage("Usage: /lb config add <message>");
									return false;
								}
							}
						} else if (player.hasPermission("lb.config.del")) {
							if (args[1].equalsIgnoreCase("del")) {
								if (!(args.length < 3)) {
									del(player, args);
									return true;
								} else {
									player.sendMessage("Usage: /lb config del <number>");
									return false;
								}
							}
						} else if (player.hasPermission("lb.config.list")) {
							if (args[1].equalsIgnoreCase("list")) {
								list(player);
								return true;
							}
						} else {
							player.sendMessage("Usage: /lb config <add|del|list>");
							return false;
						}
					} else {
						player.sendMessage("Usage: /lb config <add|del|list>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("reload")) {
					if ((!(player.hasPermission("lb.reload")))
							|| (!(player.hasPermission("lb.*")))) {
						return false;
					} else {
						reload(player);
						return true;
					}
				}
			} else if (label.equalsIgnoreCase("broadcast")) {
				if ((!(player.hasPermission("lb.broadcast")))
						|| (!(player.hasPermission("lb.*")))) {
					return false;
				} else {
					if (args.length == 0) {
						player.sendMessage(ChatColor.AQUA
								+ "I thought you wanted to say something?");
						return false;
					} else if (args.length >= 1) {
						String message = parseColors("title");
						for (String subMessage : args) {
							message += subMessage + " ";
						}
						Bukkit.broadcastMessage(message);
						return true;
					}
				}
			}
		}
		return false;
	}

	private void list(Player player) {
		String temp = ChatColor.DARK_BLUE
				+ "\n============================================\n";
		for (int x = 1; x < maxMessages; x++) {
			temp += ChatColor.GREEN + "[" + x + "]"
					+ parseColors(sm.config.getString(x + "")) + "\n";
		}
		temp += ChatColor.DARK_BLUE
				+ "============================================";
		player.sendMessage(temp);
	}

	public String parseColors(String temp) { // Method used to retrieve colors
												// properly from the config
		return ChatColor.translateAlternateColorCodes('&', temp);
	}

	private void toggle(Player player) {
		if (getToggle()) {
			setToggle(false);
			state = "off";
		} else if (!getToggle()) {
			setToggle(true);
			state = "on";
		}
		player.sendMessage("LiveBroacast was turned " + state);
	}

	private void credits(Player player) {
		player.sendMessage(ChatColor.GOLD + "\n======================"
				+ ChatColor.BLUE + "\nName: LiveBroadcast\nVersion: "
				+ pdf.getVersion() + "\nDeveloper: jwolff52" + ChatColor.GOLD
				+ "\n======================");
	}

	private void add(Player player, String[] args) {
		String message = "";
		for (int x = 2; x < args.length - 1; x++) {
			message += args[x] + " ";
		}
		message += args[args.length - 1];
		sm.config.set((maxMessages + 1) + "", message);
		message = parseColors(message);
		maxMessages++;
		sm.saveConfig();
		player.sendMessage("Message: \""
				+ sm.config.getString(maxMessages + "") + ChatColor.WHITE
				+ "\" was added to the  list!");
	}

	private void del(Player player, String[] args) {
		String message = getConfig().getString(args[2]);
		sm.config.set(args[2], "empty");
		sm.saveConfig();
		player.sendMessage("Message: \"" + message + ChatColor.WHITE
				+ "\" was removed from the list!");
	}

	private void reload(Player player) {
		setToggle(false);
		sm.reloadConfig();
		player.sendMessage(ChatColor.AQUA
				+ "LiveBroacast configuration successfully reloaded!!");
		setToggle(true);
	}

	public boolean getToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	public String getBroadcastTitle() {
		return broadcastTitle;
	}

	public void setBroadcastTitle(String broadcastTitle) {
		this.broadcastTitle = broadcastTitle;
	}
}
