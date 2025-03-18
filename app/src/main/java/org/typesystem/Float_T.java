package org.typesystem;

import org.typesystem.utils.*;

public class Float_T implements Object_T {
	float value;

	public Float_T(float value) {
		this.value = value;
	}

	@Override
	public String type() {
		return TypeList.FLOAT_OBJECT;
	}

	@Override
	public String inspect() {
		return Float.toString(this.value);
	}

	public float getValue() {
		return this.value;
	}

}
