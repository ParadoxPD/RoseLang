package org.evaluator;

import java.util.*;

import org.typesystem.*;

public class BuiltIns {
	Map<String, Builtin_Function_T> builtinFunctions;

	public BuiltIns() {
		this.builtinFunctions = new HashMap<String, Builtin_Function_T>();
		this.addFunctions();

	}

	private void addFunctions() {
		this.builtinFunctions.put("len", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {

				if (args.size() != 1) {
					return new Error_T(
							"Wrong number of arguments : Required : 1 Got " + args.size());
				}
				switch (args.getFirst()) {
					case String_T str:
						return new Integer_T(str.getValue().length());
					default:
						return new Error_T("Function len not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
	}

	// public Object_T applyFunction(String funcName, Vector<Object_T> args) {
	// return this.builtinFunctions.get(funcName).applyFunction(args);
	// }

	public Set<String> getBuiltinFunctionNames() {
		return this.builtinFunctions.keySet();
	}

	public Builtin_Function_T getBuiltIn(String funcName) {
		return this.builtinFunctions.get(funcName);
	}

	public boolean isFunctionPresent(String funcName) {
		for (String s : this.getBuiltinFunctionNames()) {
			if (funcName.equals(s)) {
				return true;
			}
		}
		return false;
	}
}
