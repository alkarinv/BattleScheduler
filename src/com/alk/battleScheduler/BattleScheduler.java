package com.alk.battleScheduler;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alk.battleScheduler.controllers.BSCommandController;
import com.alk.battleScheduler.controllers.ConfigController;
import com.alk.battleScheduler.controllers.ScheduleController;


public class BattleScheduler extends JavaPlugin {
	private static Server server = null;
	private static String pluginname; 
	private static String version;
	private static BattleScheduler plugin;

	private ScheduleController scheduler =  new ScheduleController();
	private BSCommandController commandController;
	

	public void onEnable() {
		server = getServer();
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginname = pdfFile.getName();
		version = pdfFile.getVersion();
		PluginManager pm = server.getPluginManager();

		File dir = new File(Defaults.PLUGIN_PATH);
		if (!dir.exists()){
			dir.mkdirs();}

		for (Plugin plugin : pm.getPlugins()){
			if(plugin instanceof Runnable){
				try {
					scheduler.addSchedulableEvent((JavaPlugin)plugin);
					System.out.println("Event registered: "  + plugin.getDescription().getName());
				} catch (Exception e) {}
			}        	
		}

		loadConfigFiles();
		commandController = new BSCommandController();

		/// Start the scheduler.
		scheduler.start();
		System.out.println("[" + pluginname + "]" + " version " + version + " initialized!");	
	}

	public void onDisable() {
		/// Stop the scheduler.
		scheduler.stop();
		scheduler.save();
	}

	public void loadConfigFiles() {
        ConfigController.setConfig(Util.load(getClass().getResourceAsStream(Defaults.DEFAULT_CONFIGURATION_FILE), 
        				Defaults.CONFIGURATION_FILE));
	}

	public void saveConfigFiles() {
		scheduler.save();
	}

    public static Server getBukkitServer() {return server;}
	public static String getVersion(){return version;}
	public static String getPluginName(){return pluginname;}
	public static BattleScheduler getPlugin() {return plugin;}

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.isOp() || sender.hasPermission("scheduler.admin")) {
        	commandController.handleCommand(sender, cmd, commandLabel, args);
        }
        return true;
    }

	public ScheduleController getScheduler() {
		return scheduler;
	}

}
