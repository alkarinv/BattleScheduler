package com.alk.battleScheduler.controllers;


import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.alk.battleScheduler.BattleScheduler;
import com.alk.battleScheduler.Defaults;
import com.alk.battleScheduler.objects.TimeIDOptions;
import com.alk.battleScheduler.objects.TimeNamePair;

/**
 * 
 * @author alkarin
 *
 */
public class BSCommandController  {
	ScheduleController bsi ;

	public BSCommandController(){
		bsi = BattleScheduler.getPlugin().getScheduler();	
	}

	public void handleCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String commandStr = cmd.getName().toLowerCase();
		if (Defaults.DEBUG) System.out.println("commandStr=" + commandStr);
		final int length = args.length;
		/// list command
		if (commandStr.equalsIgnoreCase("timer")){
			if (length == 0){
				showHelp(sender);
				return;
			}
			if (args[0].equalsIgnoreCase("list")){
				listEvents(sender);
			} else if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("add")){
				newEvent(sender,args);
			} else if (args[0].equalsIgnoreCase("delete")){
				deleteEvent(sender,args);
			} else if (args[0].equalsIgnoreCase("execute")){
				executeEvent(sender,args);
			} else if (args[0].equalsIgnoreCase("suspend") || args[0].equalsIgnoreCase("stop")){
				executeSuspend(sender,args);
			} else if (args[0].equalsIgnoreCase("resume")){
				executeResume(sender,args);
			} else if (args[0].equalsIgnoreCase("reload")){
				executeReload(sender,args);
			}
		} 
	}

	private void executeReload(CommandSender sender, String[] args) {
		bsi.deleteAll();
		BattleScheduler.getPlugin().loadConfigFiles();
		sendMessage(sender, "&eEvents have been reloaded from config");
	}

	private void executeSuspend(CommandSender sender, String[] args) {
		bsi.suspendEvents();
		MessageController.sendAdminMessage("&eTimer events have been suspended by &6" + ((sender==null)? "console" : sender.getName()));
		sendMessage(sender, "&eEvents have been suspended.  &6/timer resume &e to resume");
	}

	private void executeResume(CommandSender sender, String[] args) {
		bsi.resumeEvents();
		MessageController.sendAdminMessage("&eTimer events have been resumed by &6" + ((sender==null)? "console" : sender.getName()));
		sendMessage(sender, "&eEvents have been resumed");
	}
	
	private void executeEvent(CommandSender sender, String[] args) {
		if (args.length < 2) {
			showHelp(sender);
			return;
		}
		StringBuilder sb= new StringBuilder();
		for (int i=1;i<args.length;i++){
			sb.append(args[i]);
			if (i!= args.length-1) sb.append(" ");}
		String eventName = sb.toString();
		if (bsi.executeEvent(eventName)){
			sendMessage(sender,eventName + " now executing");
		} else {
			sendMessage(sender,eventName + " was not found");
		}
	}

	private void deleteEvent(CommandSender sender, String[] args) {
		if (args.length < 2) {
			showHelp(sender);
			return;
		}
		Integer i = null;
		try {			
			 i = Integer.valueOf(args[1]);
		} catch (Exception e){
			sendMessage(sender, "You need to specify an integer"); 
			return;
		}
		bsi.deleteScheduledTime(i);
		BattleScheduler.getPlugin().saveConfigFiles();
		sendMessage(sender, "&eEvent deleted");
	}


	private void newEvent(CommandSender sender, String[] args) {
		if (args.length < 2) {
			showHelp(sender);
			return;
		}
		String strarg = "";
		for (int i=1;i<args.length;i++){
			strarg += args[i] + " ";}
		strarg = strarg.replaceAll("'", "");
		TimeNamePair tnp = ConfigController.parseTime(strarg);
		if (tnp == null){
			sendMessage(sender, "you have entered a bad time!");
			return;
		}
		if ((bsi.hasEvent(tnp.name)|| bsi.consoleEvent(tnp.name)) && bsi.addScheduleTime(tnp)){
			sendMessage(sender, "&eEvent Scheduled!");
			BattleScheduler.getPlugin().saveConfigFiles();
		} else {
			sendMessage(sender, "&eEvent not recognized!");
		}
	}

	private void listEvents(CommandSender sender) {
		LinkedHashMap<String, List<TimeIDOptions>> lhm = bsi.getScheduledTimes();
		int count = 1;
		sendMessage(sender,"&e********* Scheduled Times *********");
		for (String key : lhm.keySet()){
			List<TimeIDOptions> ts = lhm.get(key);
			for (int j=0;j<ts.size();j++){
				String options = ts.get(j).getOptionString();
				sendMessage(sender,"&6 [" + count +"] '" + ts.get(j).time + " " + key+(options!=null ? " "+options : "") +"'");
				count++;
			}
		}
		sendMessage(sender,"&e********* Registered Plugins *********");
		for (String eventName : bsi.getEvents().keySet()){
			sendMessage(sender,"&6   " + eventName + " ");			
		}
	}

	private void showHelp(CommandSender sender) {
		sendMessage(sender,"&e********* Scheduler *********");
		sendMessage(sender,"&6/timer list :&f see all timers and registered events");
		sendMessage(sender,"&6/timer create <time> <event>:&f Create a timer");
		sendMessage(sender,"&e      Example:&f timer create * * * * * BattleChestEvent [options]");
		sendMessage(sender,"&6/timer delete <number>:&f Delete a timer");
		sendMessage(sender,"&6/timer execute <event>:&f Execute the given event");
	}
	
	private void sendMessage(CommandSender sender, String msg){
		MessageController.sendMessage(sender, msg);
	}

}
