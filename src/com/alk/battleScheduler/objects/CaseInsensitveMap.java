package com.alk.battleScheduler.objects;

import java.util.LinkedHashMap;

public class CaseInsensitveMap<V> extends LinkedHashMap<String, V> {
	private static final long serialVersionUID = 1L;

	@Override
	public V get(Object key){
		 return super.get(key.toString().toLowerCase());
	}

	@Override
    public V put(String key, V value) {
        return super.put(key.toString().toLowerCase(), value);
    }

	@Override
    public boolean containsKey(Object key) {
        return super.containsKey(key.toString().toLowerCase());
    }

	@Override
    public V remove(Object key) {
        return super.remove(key.toString().toLowerCase());
    }
}
