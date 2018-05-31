package io.github.jwolff52.livebroadcast.util;

import io.github.jwolff52.livebroadcast.LiveBroadcast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class LiveBroadcastTimer implements Runnable {
	
	private static LiveBroadcast lb;
	private static ArrayList<String> messages;
	
	public LiveBroadcastTimer(LiveBroadcast l) {
		lb=l;
		initMessages();
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		int configNumber = 1;
		while(true) {
			if(messages == null || messages.size() == 0) {
				lb.setToggle(false);
			}
			if (lb.getToggle()){
				Bukkit.broadcastMessage(lb.getBroadcastTitle() + lb.parseColors(messages.get(configNumber-1)));
			}
			configNumber++;
			if(configNumber > lb.getMaxMessages()) {
				initMessages();
				configNumber=1;
			}
			try {
				Thread.sleep(lb.getTimer() * 1000);
			} catch (InterruptedException e) {
				lb.logger.log(Level.WARNING, "There was an issue waiting to send the next message. This can probably be ignored", e);
			}
		}
	}
	
	public static void initMessages() {
		messages = new ArrayList<>();
		for(int i=1;i <= lb.getMaxMessages();i++) {
			messages.add(LiveBroadcast.sm.getConfig().getString(i+""));
		}
		if(lb.isRandomize()) {
			Collections.shuffle(messages);
		}
	}

}
