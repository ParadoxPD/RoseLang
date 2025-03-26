package org.typesystem;

import org.typesystem.utils.*;

public class Integer_T implements Object_T, Hashable {
	int value;

	public Integer_T(int value) {
		this.value = value;
	}

	@Override
	public String type() {
		return TypeList.INTEGER_OBJECT;
	}

	@Override
	public String inspect() {
		return Integer.toString(this.value);
	}

	@Override
	public HashKey hash() {
		return new HashKey(this.type(), Hasher.hash(this.value));
	}

	public int getValue() {
		return this.value;
	}

}
