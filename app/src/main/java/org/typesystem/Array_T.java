package org.typesystem;

import java.util.Vector;
import org.typesystem.utils.*;

public class Array_T implements Object_T {
	Vector<Object_T> value;

	public Array_T(Vector<Object_T> value) {
		this.value = value;
	}

	@Override
	public String type() {
		return TypeList.ARRAY_OBJECT;
	}

	@Override
	public String inspect() {
		String res = "[ ";
		for (Object_T obj : this.value) {
			res += obj.inspect() + ", ";
		}
		res += " ]";
		return res;
	}

	public Vector<Object_T> getValue() {
		return this.value;
	}

}
