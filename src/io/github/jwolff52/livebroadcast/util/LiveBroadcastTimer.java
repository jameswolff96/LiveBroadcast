package io.github.jwolff52.livebroadcast.util;

import io.github.jwolff52.livebroadcast.LiveBroadcast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class LiveBroadcastTimer implements Runnable {
	
	private LiveBroadcast lb;
	private ArrayList<String> messages;
	
	public LiveBroadcastTimer(LiveBroadcast lb) {
		this.lb=lb;
		if(lb.isRandomize()) {
			scrambleMessages();
		} else {
			initMessages();
		}
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		int configNumber = 1;
		while(true) {
			if (lb.getToggle()){
				Bukkit.broadcastMessage(lb.getBroadcastTitle() + lb.parseColors(messages.get(configNumber-1)));
			}
			configNumber++;
			if(configNumber > lb.getMaxMessages()) {
				if(lb.isRandomize()) {
					scrambleMessages();
				}
				configNumber=1;
			}
			try {
				Thread.sleep(lb.getTimer() * 1000);
			} catch (InterruptedException e) {
				lb.logger.log(Level.WARNING, "There was an issue waiting to send the next message. This can probably be ignored", e);
			}
		}
	}
	
	private void scrambleMessages() {
		messages = new ArrayList<>();
		for(int i=1;i <= lb.getMaxMessages();i++) {
			messages.add(LiveBroadcast.sm.getConfig().getString(i+""));
		}
		Collections.shuffle(messages);
	}
	
	private void initMessages() {
		messages = new ArrayList<>();
		for(int i=1;i <= lb.getMaxMessages();i++) {
			messages.add(LiveBroadcast.sm.getConfig().getString(i+""));
		}
	}

}
