package org.typesystem.utils;

public class HashKey {
	String type;
	long value;

	public HashKey(String type, long value) {
		this.type = type;
		this.value = value;
	}

	public long getKey() {
		return this.value;
	}

}
