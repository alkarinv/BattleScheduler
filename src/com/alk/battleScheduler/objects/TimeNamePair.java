package com.alk.battleScheduler.objects;

import java.util.ArrayList;
import java.util.List;

public class TimeNamePair {
	public String time;
	public String name;
	public List<String> options;
	public void addoptions(String string) {
		if (options==null){
			options = new ArrayList<String>();
		}
		options.add(string);
	}
	public String getOptionsString() {
		if (options == null)
			return null;
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String s: options){
			if (!first) sb.append(" ");
			sb.append(s);
			first = false;
		}
		return sb.toString();
	}
	
	public String toString(){
		return "[TNP time=" + time +" name=" + name + " args=" + getOptionsString()+"]";
	}
}
