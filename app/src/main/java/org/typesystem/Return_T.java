package org.typesystem;

public class Return_T implements Object_T {
	Object_T value;

	public Return_T(Object_T val) {
		this.value = val;
	}

	@Override
	public String inspect() {
		return this.value.inspect();
	}

	@Override
	public String type() {
		return TypeList.RETURN_OBJECT;
	}

	public Object_T getValue() {
		return this.value;
	}
}
