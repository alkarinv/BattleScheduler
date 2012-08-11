package com.alk.battleScheduler.objects;


public class TimeIDOptions {
	public String time;
	public String id;
	public String options = null;
	public TimeIDOptions(TimeNamePair tnp) {
		if (tnp.options != null)
			this.options = tnp.getOptionsString();
	}
	public String getOptionString() {
		return options;
	}
}
