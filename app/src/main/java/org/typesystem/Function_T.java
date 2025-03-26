package org.typesystem;

import org.environment.Environment;
import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;

import java.util.Vector;

public class Function_T implements Object_T {
	Vector<Identifier> parameters;
	BlockStatement body;
	Identifier name;
	Environment env;

	public Function_T(Identifier name, Vector<Identifier> params, Environment env, BlockStatement body) {
		this.name = name;
		this.parameters = params;
		this.env = env;
		this.body = body;
	}

	@Override
	public String type() {
		return TypeList.FUNCTION_OBJECT;
	}

	@Override
	public String inspect() {
		String res = "function" + " " + this.name.getNodeValue() + "( ";
		for (Identifier i : this.parameters) {
			res += i.getNodeValue() + ", ";
		}
		res += ") " + this.body.getNodeValue();
		return res;

	}

	public BlockStatement getBody() {
		return this.body;
	}

	public Environment getEnv() {
		return this.env;
	}

	public Vector<Identifier> getParameters() {
		return this.parameters;
	}
}
