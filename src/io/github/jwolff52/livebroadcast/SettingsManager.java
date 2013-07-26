package io.github.jwolff52.livebroadcast;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


public class SettingsManager {
	
	Plugin plugin;
	FileConfiguration config;
	File cfile;
	
	private SettingsManager() {}
	static SettingsManager instance=new SettingsManager();
	public static SettingsManager getInstance(){
		return instance;
	}
	public void setup(Plugin p){
		plugin=p;
		config=plugin.getConfig();
		config.options().copyDefaults(true);
		cfile=new File(plugin.getDataFolder(), "config.yml");
		saveConfig();

	}
	public FileConfiguration getConfig(){
		return config;
	}
	public void reloadConfig(){
		config=YamlConfiguration.loadConfiguration(cfile);
	}
	public void saveConfig() {
		try {
			config.save(cfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED+"Could not save LiveBroadcast config.yml!");
			e.printStackTrace();//fitzcraft.mcph.co:25848
		}
	}
}
