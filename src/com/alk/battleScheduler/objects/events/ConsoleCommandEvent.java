package com.alk.battleScheduler.objects.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ConsoleCommandEvent implements SchedulableEvent{
	String runString = null;
	Plugin plugin = null;
	public ConsoleCommandEvent(Plugin plugin, List<String> consoleCommand) {
		this.plugin = plugin;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s: consoleCommand){
			if (!first) sb.append(" ");
			sb.append(s);
			first = false;
		}
		runString = sb.toString();
	}

	public ConsoleCommandEvent(Plugin plugin, String command) {
		this.plugin = plugin;
		runString = command;
	}
	
	public void run() {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), runString);		
	}

	public String getName() {
		return plugin != null ? plugin.getDescription().getName() : null;
	}

}
