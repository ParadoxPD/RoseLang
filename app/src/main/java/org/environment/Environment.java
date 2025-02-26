package org.environment;

import org.typesystem.*;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	Map<String, Object_T> store;

	public Environment() {
		this.store = new HashMap<String, Object_T>();

	}

	public Object_T getObject(String name) {
		if (this.store.containsKey(name))
			return this.store.get(name);
		else
			return null;
	}

	public void setObject(String name, Object_T obj) {
		this.store.put(name, obj);
	}

	public boolean isObjectPresent(String name) {
		return this.store.containsKey(name);
	}

	public void printObjects() {
		System.out.println("Objects : " + this.store.size());
		for (String s : this.store.keySet()) {
			System.out.println(s + " : " + this.store.get(s));

		}
	}
}
