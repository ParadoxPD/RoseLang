package org.typesystem;

import org.typesystem.utils.*;

public class Null_T implements Object_T {
	@Override
	public String type() {
		return TypeList.NULL_OBJECT;
	}

	@Override
	public String inspect() {
		return "null";
	}
}
