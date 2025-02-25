package org.typesystem;

public class Integer_T implements Object_T {
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

	public int getValue() {
		return this.value;
	}

}
