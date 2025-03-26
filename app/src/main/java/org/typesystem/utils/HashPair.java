package org.typesystem.utils;

import org.typesystem.*;

public class HashPair {
	Object_T key;
	Object_T value;

	public HashPair(Object_T key, Object_T value) {
		this.key = key;
		this.value = value;
	}

	public Object_T getKey() {
		return this.key;
	}

	public Object_T getValue() {
		return this.value;
	}
}
