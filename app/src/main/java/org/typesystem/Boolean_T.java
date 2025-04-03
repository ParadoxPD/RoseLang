package org.typesystem;

import org.typesystem.utils.*;

public class Boolean_T implements Object_T, Primitive, Hashable {
	boolean value;

	public Boolean_T(boolean value) {
		this.value = value;
	}

	@Override
	public String type() {
		return TypeList.BOOLEAN_OBJECT;
	}

	@Override
	public String inspect() {
		return this.value ? "true" : "false";
	}

	@Override
	public HashKey hash() {
		return new HashKey(this.type(), Hasher.hash(this.value));
	}

	public boolean getValue() {
		return this.value;
	}

}
