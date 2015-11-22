package io.github.jwolff52.livebroadcast;

public class Driver {
	public static void main(String[] args) {
		int minTime = 1, maxTime = 30, maxPlayers = 10, onlinePlayers = 20;
		int time = (int)(maxTime - (maxTime * (onlinePlayers/(double)maxPlayers)));
		System.out.println(time);
	}
}
