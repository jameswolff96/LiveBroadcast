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

package io.github.jwolff52.livebroadcast.util;

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
		cfile=new File(plugin.getDataFolder(), "config.yml");
		p.saveDefaultConfig();
		config.addDefault("randomize", false);
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
			Bukkit.getServer().getLogger().severe(ChatColor.RED+"Could not save "+plugin.getDescription().getName()+" config.yml!");
			e.printStackTrace();
		}
	}
}
