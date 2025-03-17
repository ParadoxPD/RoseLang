package org.typesystem.utils;

import java.util.Objects;

public class Hasher {

	public static long hash(String obj) {
		return Objects.hash(obj);
	}

	public static long hash(int obj) {
		return (long) obj;
	}

	public static long hash(boolean obj) {
		return obj ? 1 : 0;
	}
}
