package io.github.jwolff52.livebroadcast.util;

import io.github.jwolff52.livebroadcast.LiveBroadcast;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LiveBroadcastListener implements Listener{

	private LiveBroadcast lb;
	
	public LiveBroadcastListener(LiveBroadcast lb) {
		this.lb=lb;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(lb.getServer().getOnlinePlayers().size() == 1) {
			if(!lb.getToggle()) {
				lb.toggle(lb.getServer().getConsoleSender());
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(lb.getServer().getOnlinePlayers().size() == 1) {
			if(lb.getToggle()) {
				lb.toggle(lb.getServer().getConsoleSender());
			}
		}
	}
}
