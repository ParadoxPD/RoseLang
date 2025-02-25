package org.typesystem;

public class Boolean_T implements Object_T {
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

	public boolean getValue() {
		return this.value;
	}
}
