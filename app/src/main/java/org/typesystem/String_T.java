package org.typesystem;

import org.typesystem.utils.*;

public class String_T implements Object_T, Hashable {
	String value;

	public String_T(String value) {
		this.value = value;
	}

	@Override
	public String type() {
		return TypeList.STRING_OBJECT;
	}

	@Override
	public String inspect() {
		return this.value;
	}

	@Override
	public HashKey hash() {
		return new HashKey(this.type(), Hasher.hash(this.value));
	}

	public String getValue() {
		return this.value;
	}

}
