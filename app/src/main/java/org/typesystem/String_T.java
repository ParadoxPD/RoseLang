package org.typesystem;

public class String_T implements Object_T {
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

	public String getValue() {
		return this.value;
	}

}
