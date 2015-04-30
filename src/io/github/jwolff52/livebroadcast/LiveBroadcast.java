/**************************************************************************
    LiveBroadcast - Automatic Broadcast Plugin for CraftBukkit
    Copyright (C) 2014-2015  James Wolff

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
**************************************************************************/

package io.github.jwolff52.livebroadcast;

import io.github.jwolff52.livebroadcast.util.LiveBroadcastListener;
import io.github.jwolff52.livebroadcast.util.LiveBroadcastTimer;
import io.github.jwolff52.livebroadcast.util.SettingsManager;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public final class LiveBroadcast extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");

	public static LiveBroadcast plugin;

	public static SettingsManager sm;

	private static PluginDescriptionFile pdf;
	
	private LiveBroadcastListener lbl;

	private String state, broadcastTitle;

	private boolean toggle = false, useScalableTimer, randomize;

	private int minTime, maxTime, maxPlayers, configNumber = 1, maxMessages = 0;

	@Override
	public void onEnable() {
		pdf = getDescription();
		sm = SettingsManager.getInstance();
		sm.setup(this);
		
		lbl=new LiveBroadcastListener(this);
		
		getServer().getPluginManager().registerEvents(lbl, this);

		while (true) {
			if (sm.getConfig().getString(configNumber + "") != null) {
				configNumber++;
				maxMessages++;
			} else {
				configNumber = 1;
				break;
			}
		}

		setBroadcastTitle(parseColors(sm.getConfig().getString("title")));
		minTime = getConfig().getConfigurationSection("timer").getInt("min_time");
		maxTime = getConfig().getConfigurationSection("timer").getInt("max_time");
		maxPlayers = getConfig().getConfigurationSection("timer").getInt("min_time");
		useScalableTimer = getConfig().getConfigurationSection("timer").getBoolean("use_scalable_timer");
		setRandomize(getConfig().getBoolean("randomize"));
		if(getServer().getOnlinePlayers().size() > 0) {
			toggle = true;
		}
		new LiveBroadcastTimer(this);
		/************************************************************
		// Scheduler that prints the messages every 'timer' seconds
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					
			@Override
			public void run() {
				if (sm.getConfig().getString(configNumber + "") != null) {
					if (!sm.getConfig().getString(configNumber + "").equalsIgnoreCase("empty")) {
						if (getToggle()){
							Bukkit.broadcastMessage(broadcastTitle + parseColors(sm.getConfig().getString(configNumber + "")));
						}
					}
					configNumber++;
				} else {
					configNumber = 1;
				}
			}
		}, 0L, getTimer());
		/*************************************************************/
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			logger.warning("[LiveBroadcast] There was an error starting plugin metrics!");
		}
		this.logger.info(pdf.getName() + " Version: " + pdf.getVersion()
				+ " has been enabled!");
		super.onEnable();
	}

	@Override
	public void onDisable() {
		pdf = getDescription();
		this.logger.info(pdf.getName() + " has been Disabled!");
		super.onDisable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof ConsoleCommandSender) {
			if(cmd.getName().equals("lbtoggle")){
				toggle(sender);
				return true;
			}else if(cmd.getName().equals("lbcredits")){
				credits(sender);
				return true;
			}else if(cmd.getName().equals("lbadd")){
				if(args.length<1){
					sender.sendMessage(broadcastTitle+"Usage: /lbadd <message>");
					return false;
				}
				add(sender, args);
				return true;
			}else if(cmd.getName().equals("lbdel")){
				if(args.length<1){
					sender.sendMessage(broadcastTitle+"Usage: /lbdel <message_number>");
					return false;
				}
				try{
					del(sender, args);
					return true;
				}catch(NumberFormatException e){
					sender.sendMessage(broadcastTitle+"Usage: /lbdel <message_number>");
					return false;
				}
			}else if(cmd.getName().equals("lblist")){
				if(args.length<1){
					list(sender, "1");
					return true;
				}
				try{
					list(sender, args[0]);
					return true;
				}catch(NumberFormatException e){
					sender.sendMessage(broadcastTitle+"Usage: /lblist [page]");
					return false;
				}
			}else if(cmd.getName().equals("lbreload")){
				reload(sender);
				return true;
			}else if(cmd.getName().equals("lbbroadcast")){
				if (args.length == 0) {
					sender.sendMessage(broadcastTitle + ChatColor.AQUA+"I thought you wanted to say something?");
					sender.sendMessage(broadcastTitle + ChatColor.DARK_RED + "Usage: /lbbroadcast <word> [word2] [word3]...");
					return false;
				} else if (args.length >= 1) {
					broadcast(args);
					return true;
				}
			}
		} else {
			if(cmd.getName().equals("lbtoggle")){
				if (!sender.hasPermission("lb.toggle") || sender instanceof ConsoleCommandSender) {
					sender.sendMessage(parseColors(sm.getConfig().getString("title"))+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				} else {
					toggle(sender);
					return true;
				}
			}else if(cmd.getName().equals("lbcredits")){
				if (!sender.hasPermission("lb.credits") || sender instanceof ConsoleCommandSender) {
					sender.sendMessage(broadcastTitle+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				} else {
					credits(sender);
					return true;
				}
			}else if(cmd.getName().equals("lbadd")){
				if(!sender.hasPermission("lb.config.add")) {
					sender.sendMessage(broadcastTitle+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				}else{
					if(args.length<1){
						sender.sendMessage(broadcastTitle+"Usage: /lbadd <message>");
						return false;
					}
					add(sender, args);
					return true;
				}
			}else if(cmd.getName().equals("lbdel")){
				if(!sender.hasPermission("lb.config.del")) {
					sender.sendMessage(broadcastTitle+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				}else{
					if(args.length<1){
						sender.sendMessage(broadcastTitle+"Usage: /lbdel <message_number>");
						return false;
					}
					try{
						del(sender, args);
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(broadcastTitle+"Usage: /lbdel <message_number>");
						return false;
					}
				}
			}else if(cmd.getName().equals("lblist")){
				if(!sender.hasPermission("lb.config.list")) {
					sender.sendMessage(broadcastTitle+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				}else{
					if(args.length<1){
						list(sender, "1");
						return true;
					}
					try{
						list(sender, args[0]);
						return true;
					}catch(NumberFormatException e){
						sender.sendMessage(broadcastTitle+"Usage: /lblist [page]");
						return false;
					}
				}
			}else if(cmd.getName().equals("lbreload")){
				if (!sender.hasPermission("lb.reload")) {
					sender.sendMessage(broadcastTitle+ChatColor.DARK_RED+"You do not have permission to preform this command!");
					return false;
				} else {
					reload(sender);
					return true;
				}
			}else if(cmd.getName().equals("lbbroadcast")){
				if (!sender.hasPermission("lb.broadcast")) {
					return false;
				} else {
					if (args.length == 0) {
						sender.sendMessage(broadcastTitle+ChatColor.AQUA+"I thought you wanted to say something?");
						sender.sendMessage(broadcastTitle + ChatColor.DARK_RED + "Usage: /lbbroadcast <word> [word2] [word3]...");
						return false;
					} else if (args.length >= 1) {
						broadcast(args);
						return true;
					}
				}
			}
		}
		return super.onCommand(sender, cmd, label, args);
	}

	public void toggle(CommandSender sender) {
		if (getToggle()) {
			setToggle(false);
			state = "off";
		} else if (!getToggle()) {
			setToggle(true);
			state = "on";
		}
		sender.sendMessage(broadcastTitle+"LiveBroacast was turned " + state);
	}

	private void credits(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "\n======================" + ChatColor.BLUE + "\nName: LiveBroadcast\nVersion: " + pdf.getVersion() + "\nDeveloper: jwolff52" + ChatColor.GOLD + "\n======================");
	}

	private void add(CommandSender sender, String[] args) {
		String message = "";
		for (int x = 0; x < args.length - 1; x++) {
			message += args[x] + " ";
		}
		message += args[args.length - 1];
		sm.getConfig().set((maxMessages + 1) + "", message);
		message = parseColors(message);
		maxMessages++;
		sm.saveConfig();
		sender.sendMessage(broadcastTitle+"Message: " + ChatColor.RESET + "\"" + sm.getConfig().getString(maxMessages + "") + ChatColor.WHITE + "\" was added to the  list!");
	}

	private void del(CommandSender sender, String[] args) throws NumberFormatException{
		int cNumber=Integer.valueOf(args[0]);
		String message = getConfig().getString(args[0]);
		for(int x=1;x<cNumber;x++){
			sm.getConfig().set(x + "", sm.getConfig().getString(x + ""));
		}
		for(int x=cNumber;x<maxMessages;x++){
			sm.getConfig().set(x + "", sm.getConfig().getString((x + 1) + ""));
		}
		sm.getConfig().set(maxMessages + "", null);
		maxMessages--;
		sm.saveConfig();
		sender.sendMessage(broadcastTitle+"Message: " + ChatColor.RESET + "\"" + message + ChatColor.WHITE + "\" was removed from the list!");
	}

	private void list(CommandSender sender, String page) throws NumberFormatException{
		if(sender instanceof Player){
			int intPage=Integer.valueOf(page)-1;
			String temp = ChatColor.DARK_BLUE + "=== LiveBroadcast ==Page " + (intPage + 1) +"/" + ((maxMessages/10) + 1) + "================";
			for (int x = (intPage*10)%maxMessages; x < (intPage*10)+10 && x < maxMessages; x++) {
				temp += "\n" + ChatColor.GREEN + "[" + (x + 1) + "] " + ChatColor.RESET + parseColors(sm.getConfig().getString(x + 1 + ""));
			}
			sender.sendMessage(temp);
		}else{
			String temp = ChatColor.DARK_BLUE + "=== LiveBroadcast =========================";
			for (int x = 0; x < maxMessages; x++) {
				temp += "\n" + ChatColor.GREEN + "[" + (x + 1) + "] " + ChatColor.RESET + parseColors(sm.getConfig().getString(x + 1 + ""));
			}
			sender.sendMessage(temp);
		}
	}

	private void reload(CommandSender sender) {
		setToggle(false);
		sm.reloadConfig();
		int tempConfigNumber=1;
		int tempMaxMessages=0;
		while (true) {
			if (sm.getConfig().getString(tempConfigNumber + "") != null) {
				tempConfigNumber++;
				tempMaxMessages++;
			} else {
				break;
			}
		}
		setBroadcastTitle(parseColors(getConfig().getString("title")));
		maxMessages=tempMaxMessages;
		minTime = getConfig().getConfigurationSection("timer").getInt("min_time");
		maxTime = getConfig().getConfigurationSection("timer").getInt("max_time");
		maxPlayers = getConfig().getConfigurationSection("timer").getInt("min_time");
		useScalableTimer = getConfig().getConfigurationSection("timer").getBoolean("use_scalable_timer");
		setRandomize(getConfig().getBoolean("randomize"));
		sender.sendMessage(broadcastTitle+ChatColor.AQUA + "LiveBroacast configuration successfully reloaded!!");
		setToggle(true);
	}
	
	private void broadcast(String[] args){
		String message = broadcastTitle;
		for (String subMessage : args) {
			message += subMessage + " ";
		}
		Bukkit.broadcastMessage(message);
	}

	public String parseColors(String temp) {
		return ChatColor.translateAlternateColorCodes('&', temp);
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
		if(!broadcastTitle.endsWith(" ")) {
			this.broadcastTitle = broadcastTitle + " ";
			return;
		}
		this.broadcastTitle = broadcastTitle;
	}

	public long getTimer() {
		if(useScalableTimer) {
			int time = (int)(maxTime - (maxTime * (getServer().getOnlinePlayers().size()/(double)maxPlayers)));
			if(time < minTime) {
				return minTime;
			}
			return time;
		} else {
			return minTime;
		}
	}

	public int getMaxMessages() {
		return maxMessages;
	}

	public boolean isRandomize() {
		return randomize;
	}

	public void setRandomize(boolean randomize) {
		this.randomize = randomize;
	}
}
