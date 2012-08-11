package com.alk.battleScheduler.controllers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alk.battleScheduler.Util;

public class MessageController {

	public static void sendMessage(CommandSender sender, String msg){
		if (sender == null){
			System.out.println(Util.deColorChat(msg));
		} else {
			sender.sendMessage(Util.colorChat(msg));
		}
	}

	public static void sendAdminMessage(String msg) {
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player p: players){
			if (p.isOp()){
				sendMessage(p,msg);}
		}
	}

}
