package io.github.jwolff52.livebroadcast.util;

import io.github.jwolff52.livebroadcast.LiveBroadcast;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class LiveBroadcastTimer implements Runnable {
	
	private LiveBroadcast lb;
	
	public LiveBroadcastTimer(LiveBroadcast lb) {
		this.lb=lb;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		int configNumber = 1;
		while(true) {
			if (lb.getToggle()){
				Bukkit.broadcastMessage(lb.getBroadcastTitle() + lb.parseColors(LiveBroadcast.sm.getConfig().getString(configNumber + "")));
			}
			configNumber++;
			if(configNumber > lb.getMaxMessages()) {
				configNumber=1;
			}
			try {
				Thread.sleep(lb.getTimer() * 1000);
			} catch (InterruptedException e) {
				lb.logger.log(Level.WARNING, "There was an issue waiting to send the next message. This can probably be ignored", e);
			}
		}
	}

}
