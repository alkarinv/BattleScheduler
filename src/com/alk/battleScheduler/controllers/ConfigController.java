package com.alk.battleScheduler.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.alk.battleScheduler.BattleScheduler;
import com.alk.battleScheduler.Defaults;
import com.alk.battleScheduler.objects.TimeIDOptions;
import com.alk.battleScheduler.objects.TimeNamePair;
/**
 * 
 * @author alkarin
 *
 */

public class ConfigController {
	public static YamlConfiguration config;
	static File f = null;

	private static Pattern pattern = Pattern.compile("[ \t]+");
    
    public static boolean getBoolean(String node) {return config.getBoolean(node, false);}
    public static  String getString(String node) {return config.getString(node);}
    public static int getInt(String node) {return config.getInt(node, 0);}
    public static double getDouble(String node) {return config.getDouble(node, -1);}


    public static String formattedBalance(double balance) {
    	return balance + "";
    }

    public static void setConfig(File f){
		ConfigController.f = f;
		config = new YamlConfiguration();
		try {
			config.load(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	List<?> nodes = config.getList("times");
		BattleScheduler bs = BattleScheduler.getPlugin();
//		System.out.println(" bs  here " + nodes);
		if (nodes != null){
	    	for (Object strtime: nodes){
//	    		System.out.println(" here " + strtime);
	    		TimeNamePair tnp = parseTime((String)strtime);
	    		if (tnp == null) {
	    			continue;}

	    		bs.getScheduler().addScheduleTime(tnp);
	    	}			
		}
    }

    public static TimeNamePair parseTime(String strtime){
		if (Defaults.DEBUG) System.out.println("time=" + strtime);
		TimeNamePair tnp = new TimeNamePair();
		try {
			String[] ar = pattern.split(strtime);
			if (Defaults.DEBUG) for (int i=0;i<ar.length;i++){System.out.println("s["+i+"]="+ar[i]);}
			StringBuilder sb = new StringBuilder();
			for (int i=0;i<5;i++){
				sb.append(ar[i] +" ");}
			tnp.time = sb.toString();
			tnp.name= ar[5];
			for (int i=6;i<ar.length;i++){
				tnp.addoptions(ar[i]);}
		} catch (Exception e){
			System.err.println("Error parsing the time " + strtime);
			return null;
		}
		return tnp;
    }
	
    public static void save(LinkedHashMap<String, List<TimeIDOptions>> lhm ){
    	List<String> times = new ArrayList<String>();
    	for (String key : lhm.keySet()){
			List<TimeIDOptions> ts = lhm.get(key);
			for (int j=0;j<ts.size();j++){
				String options = ts.get(j).getOptionString();
				if (Defaults.DEBUG) System.out.println("time=" + ts.get(j).time + key+(options!=null ? " "+options : ""));
				times.add(ts.get(j).time + key+(options!=null ? " "+options : ""));
			}
		}
    	config.set("times",times);
    	try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
