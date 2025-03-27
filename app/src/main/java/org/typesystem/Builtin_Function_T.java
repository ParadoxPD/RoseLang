package org.typesystem;

import java.util.Vector;

import org.typesystem.utils.*;

public abstract class Builtin_Function_T implements Object_T {

	public Builtin_Function_T() {
	}

	@Override
	public String type() {
		return TypeList.BUILTIN_FUNCTION_OBJECT;
	}

	@Override
	public String inspect() {
		return "Builtin Function";
	}

	public abstract Object_T applyFunction(Vector<Object_T> args);

}
