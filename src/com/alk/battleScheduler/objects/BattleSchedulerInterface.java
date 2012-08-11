package com.alk.battleScheduler.objects;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.alk.battleScheduler.objects.events.SchedulableEvent;


public interface BattleSchedulerInterface {
	public void addSchedulableEvent(JavaPlugin plugin) throws EventNotFoundException;
	public void addSchedulableEvent(SchedulableEvent event) throws EventNotFoundException;
	public PluginDescriptionFile getDescription();
}
