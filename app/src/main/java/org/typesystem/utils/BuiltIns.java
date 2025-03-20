package org.typesystem.utils;

import java.util.*;

import org.evaluator.Evaluator;
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

				int argCount = 1;
				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}

				switch (args.getFirst()) {
					case String_T str:
						return new Integer_T(str.getValue().length());
					case Array_T arr:
						return new Integer_T(arr.getValue().size());
					default:
						return new Error_T("Function len not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("first", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {

				int argCount = 1;
				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}

				switch (args.getFirst()) {
					case Array_T arr:
						return arr.getValue().getFirst();
					default:
						return new Error_T("Function first not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("last", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 1;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getFirst()) {
					case Array_T arr:
						return arr.getValue().getLast();
					default:
						return new Error_T("Function last not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("push", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 2;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getFirst()) {
					case Array_T arr:
						arr.getValue().addLast(args.get(1));
						return arr;
					default:
						return new Error_T("Function push not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("pop", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 1;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getFirst()) {
					case Array_T arr:
						Object_T elem = arr.getValue().getLast();
						arr.getValue().removeLast();
						return elem;
					default:
						return new Error_T("Function pop not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("shift", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 1;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getFirst()) {
					case Array_T arr:
						Object_T elem = arr.getValue().getFirst();
						arr.getValue().removeFirst();
						return elem;
					default:
						return new Error_T("Function shift not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("print", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				for (Object_T arg : args) {
					System.out.println(arg.inspect());
				}
				return new Null_T();
			}

		});
		this.builtinFunctions.put("map", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 2;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getLast()) {
					case Array_T arr:
						Object_T func = args.getFirst();
						if (func instanceof Function_T) {
							Vector<Object_T> res = new Vector<Object_T>();
							Evaluator eval = new Evaluator();

							for (Object_T obj : arr.getValue()) {
								Vector<Object_T> funcArgs = new Vector<Object_T>();
								funcArgs.add(obj);
								res.add(eval.applyFunction(func, funcArgs));
							}
							return new Array_T(res);
						} else {
							return new Error_T("Second argument should be a function");
						}
					default:
						return new Error_T("Function map not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("filter", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 2;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getLast()) {
					case Array_T arr:
						Object_T func = args.getFirst();
						if (func instanceof Function_T) {
							Vector<Object_T> res = new Vector<Object_T>();
							Evaluator eval = new Evaluator();

							for (Object_T obj : arr.getValue()) {
								Vector<Object_T> funcArgs = new Vector<Object_T>();
								funcArgs.add(obj);
								Object_T out = eval.applyFunction(func, funcArgs);
								if (out instanceof Boolean_T) {

									if (((Boolean_T) out).getValue()) {
										res.add(obj);
									}
								}

							}
							return new Array_T(res);
						} else {
							return new Error_T("Second argument should be a function");
						}
					default:
						return new Error_T("Function map not supported for type : "
								+ args.getFirst().type());
				}
			}
		});
		this.builtinFunctions.put("sum", new Builtin_Function_T() {
			@Override
			public Object_T applyFunction(Vector<Object_T> args) {
				int argCount = 1;

				if (args.size() != argCount) {
					return new Error_T(
							"Wrong number of arguments : Required : " + argCount + " Got "
									+ args.size());
				}
				switch (args.getFirst()) {
					case Array_T arr:
					float a = 0;
					int b = 0;
					for(Object_T obj:arr.getValue()){
						if(obj instanceof Integer_T){
							b+=((Integer_T)obj).getValue();
						}
						else if(obj instanceof Float_T){
							a+=((Float_T)obj).getValue();
						}
					}
					if(a>0){
						return new Float_T(a+b);
					}
					else{
						return new Integer_T(b);
					}
					default:
						return new Error_T("Function shift not supported for type : "
								+ args.getFirst().type());
				}


				
			}

		});


		// TODO: Add more builtins like sum , substr, ... for array and
		// string

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
