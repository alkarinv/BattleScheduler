package com.alk.battleScheduler.objects.events;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;

import com.alk.battleScheduler.BattleScheduler;
import com.alk.battleScheduler.Defaults;

public class ServerMessageEvent implements Runnable{
	private static final String version = "1.0";
	private static final String name = "ServerMessageEvent";
	private static final PluginDescriptionFile pdf = new PluginDescriptionFile(name,version,null);

	String message;
	public ServerMessageEvent(String s){
		this.message = s;
	}

	public void run() {
		if (Defaults.DEBUG) System.out.println(name + " running " + message);
		Server server = BattleScheduler.getBukkitServer();
		server.broadcastMessage(message);
	}

	public PluginDescriptionFile getDescription() {return pdf;}

}
