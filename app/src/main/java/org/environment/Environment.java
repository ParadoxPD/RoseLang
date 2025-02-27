package org.environment;

import org.typesystem.*;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	Map<String, Object_T> store;
	Environment outerEnv;

	public Environment(Environment outer) {
		this();
		this.outerEnv = outer;
	}

	public Environment() {
		this.store = new HashMap<String, Object_T>();
		this.outerEnv = null;

	}

	public Object_T getObject(String name) {
		if (this.store.containsKey(name))
			return this.store.get(name);
		else if (this.outerEnv != null) {
			return this.outerEnv.getObject(name);
		} else
			return null;
	}

	public void assignObject(String name, Object_T obj) {
		this.store.put(name, obj);
	}

	public void reAssignObject(String name, Object_T obj) {
		if (this.store.containsKey(name))
			this.store.put(name, obj);
		else if (this.outerEnv != null) {
			this.outerEnv.reAssignObject(name, obj);
		}
	}

	public Environment setOuterEnv() {
		return this.outerEnv;
	}

	public boolean isObjectPresent(String name) {
		if (this.outerEnv != null) {
			return this.store.containsKey(name) || this.outerEnv.isObjectPresent(name);
		} else
			return this.store.containsKey(name);
	}

	public void printObjects() {
		System.out.println("Objects : " + this.store.size());
		if (this.outerEnv != null) {
			this.outerEnv.printObjects();
		}
		for (String s : this.store.keySet()) {
			System.out.println(s + " : " + this.store.get(s));

		}
	}
}
