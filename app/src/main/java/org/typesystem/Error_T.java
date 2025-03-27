package org.typesystem;

import org.typesystem.utils.*;

public class Error_T implements Object_T {
	String message;

	public Error_T(String message) {
		this.message = message;
	}

	@Override
	public String type() {
		return TypeList.ERROR_OBJECT;
	}

	@Override
	public String inspect() {
		return "ERROR :" + this.message;
	}
}
