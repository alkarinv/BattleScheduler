package com.alk.battleScheduler.controllers;

import it.sauronsoftware.cron4j.Scheduler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.alk.battleScheduler.BattleScheduler;
import com.alk.battleScheduler.Defaults;
import com.alk.battleScheduler.objects.CaseInsensitveMap;
import com.alk.battleScheduler.objects.EventNotFoundException;
import com.alk.battleScheduler.objects.TimeIDOptions;
import com.alk.battleScheduler.objects.TimeNamePair;
import com.alk.battleScheduler.objects.events.ConsoleCommandEvent;
import com.alk.battleScheduler.objects.events.SchedulableEvent;


public class ScheduleController {
	static boolean suspendEvents = false;

	/**
	 * Kind of a round about way of trying to keep everything semi synced with the main thread
	 */
	public class RunInstance implements Runnable{
		Runnable runnable;
		JavaPlugin jp;
		String name;
		public RunInstance(String name, Runnable r, JavaPlugin jp){
			this.runnable = r;
			this.jp = jp;
			this.name = name;
		}
		public void run() {
			if (suspendEvents){
				MessageController.sendAdminMessage("&4[BattleScheduler]&eNot starting &6" + name+"&e b/c events are suspended. &6/timer resume,&e to resume");
			} else {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(jp, runnable);				
			}
		}	
	}

	static Scheduler s = new Scheduler();
	Map<String, Runnable> events = new CaseInsensitveMap<Runnable>();
	LinkedHashMap<String, List<TimeIDOptions>> times = new CaseInsensitveMap<List<TimeIDOptions>>();

	public void start(){
		s.start();
	}

	public void stop(){
		s.stop();	
	}
	public void save() {
		ConfigController.save(times);		
	}

	public LinkedHashMap<String, List<TimeIDOptions>> getScheduledTimes() {return times;}
	public Map<String, Runnable> getEvents(){return events;}
	public boolean hasEvent(String name) {return events.containsKey(name);}
	public boolean consoleEvent(String name){return getPlugin(name) != null;}

	public boolean addScheduleTime(TimeNamePair tnp) {
		final String time = tnp.time;
		final String name = tnp.name;
		Runnable r = events.get(name);
		/// if no event, perhaps its a console command event
		if (r == null || tnp.options != null){
			r = getConsoleCommand(tnp);
		}
		/// we truly have no event, get out of here
		if (r==null){
			return false;
		}
		/// Get the times for this event
		List<TimeIDOptions> ts = getTimes(name);
		try{
			RunInstance re = new RunInstance(name, r, BattleScheduler.getPlugin());

			TimeIDOptions tid = new TimeIDOptions(tnp);
			tid.time = time;
			tid.id = s.schedule(time, re);
			ts.add(tid);
			System.out.println("[" + BattleScheduler.getPluginName() + "] scheduling '" + tid.time + " " + name + " args=" + tnp.getOptionsString());
		} catch (Exception e){
			System.err.println("[" + BattleScheduler.getPluginName() + "] error scheduling " + name +"  tnp=" + tnp);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Runnable getConsoleCommand(TimeNamePair tnp) {
		final String name = tnp.name;
		Plugin plugin = getPlugin(name);
		if (plugin == null){
			System.out.println("[" + BattleScheduler.getPluginName() + "] can't find plugin " + name);
			return null;
		}

		ConsoleCommandEvent re = new ConsoleCommandEvent(plugin, tnp.options);		
		return re;
	}
	private Plugin getPlugin(String name){
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
		if (plugin != null)
			return plugin;
		Plugin plugins[] = Bukkit.getServer().getPluginManager().getPlugins();
		for (Plugin pl : plugins){
			if (pl.getDescription().getName().equalsIgnoreCase(name))
				return pl;
		}
		return null;
	}
	private List<TimeIDOptions> getTimes(String name){
		List<TimeIDOptions> ts = times.get(name);
		if (ts == null){
			ts = new ArrayList<TimeIDOptions>();
			times.put(name, ts);
		}
		return ts;
	}
	
	public void deleteAll() {
		for (List<TimeIDOptions> ts : times.values()){
			for (TimeIDOptions tid: ts){
				s.deschedule(tid.id);
			}
		}
		times.clear();
	}

	public void deleteScheduledTime(int i){
		int count = 1;
		for (List<TimeIDOptions> ts : times.values()){
			for (int j=0;j<ts.size();j++){
				if (count == i){
					TimeIDOptions tid = ts.get(j);
					ts.remove(j);
					if (Defaults.DEBUG) System.out.println("Tid=" + tid.id + " tid=" + tid.time);
					if (tid.id != null && !tid.id.isEmpty())
						s.deschedule(tid.id);
					return;
				}
				count++;
			}
		}
	}

	public boolean executeEvent(String eventName) {
		Runnable r = events.get(eventName);
		if (r== null){
			return false;}
		new Thread(r).start();
		return true;
	}

	public void addSchedulableEvent(JavaPlugin se) throws EventNotFoundException{
		String name = se.getDescription().getName();
		if (! (se instanceof Runnable)){
			throw new EventNotFoundException( name + " is not a Scheduable event plugin!");}

		Runnable r = ((Runnable)se);
		addSchedulableEvent(r, name);
	}

	public void addSchedulableEvent(SchedulableEvent se) throws EventNotFoundException{
		String name = se.getName();
		addSchedulableEvent(se, name);
	}

	private void addSchedulableEvent(Runnable runit, String name) throws EventNotFoundException {
		RunInstance r = new RunInstance(name, runit, BattleScheduler.getPlugin());
		String key = name;
		events.put(key,r);
		if (!times.containsKey(key)){
			//			System.err.println("The event " + name + " is not scheduled at any time");
			return;
		}

		List<TimeIDOptions> stimes = times.get(key);
		for (TimeIDOptions tid: stimes){
			if (Defaults.DEBUG) System.out.println("Scheduling " + name + "  at " + tid.time);
			try {
				tid.id = s.schedule(tid.time, r);
			} catch (Exception e){
				throw new EventNotFoundException("The time to start '" + tid.time + "' contact an admin to fix it");
			}
		}
	}

	public void suspendEvents() {
		suspendEvents = true;
	}

	public void resumeEvents() {
		suspendEvents = false;		
	}



}
