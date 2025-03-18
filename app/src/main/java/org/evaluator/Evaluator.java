package org.evaluator;

import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.typesystem.*;
import org.typesystem.utils.*;
import org.parser.literals.*;
import org.environment.*;
import org.error.*;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Evaluator {
	private Vector<EvaluatorError> errors;
	private BuiltIns builtinFunctions;

	public Evaluator() {

		this.errors = new Vector<EvaluatorError>();
		this.builtinFunctions = new BuiltIns();
	}

	public Object_T eval(Node node, Environment env) {
		switch (node) {
			// NOTE: Statement Evaluation
			case Program p:
				return this.evalProgram(p.getStatements(), env);
			case ExpressionStatement e:
				return this.eval(e.getExpression(), env);
			case BlockStatement bs:
				return evalBlockStatement(bs, env);
			case IfExpression ie:
				return evalIfExpression(ie, env);
			case ReturnStatement rs:
				Object_T val = this.eval(rs.getReturnValue(), env);
				if (this.isError(val))
					return val;
				return new Return_T(val);
			case LetStatement ls:
				return this.evalLetStatement(ls, env);
			case AssignmentStatement as:
				return this.evalAssignmentStatement(as, env);
			case WhileStatement ws:
				return this.evalWhileStatement(ws, env);

			// NOTE: Expression Evaluation
			case PrefixExpression pe:
				Object_T right = this.eval(pe.getRight(), env);
				if (this.isError(right))
					return right;
				return this.evalPrefixExpression(pe.getOperator(), right);
			case InfixExpression ie when !(ie instanceof DotExpression):
				Object_T left = this.eval(ie.getLeft(), env);
				if (this.isError(left))
					return left;
				right = this.eval(ie.getRight(), env);
				if (this.isError(right))
					return right;
				return evalInfixExpression(ie.getOperator(), left, right);
			case IndexExpression ie:
				left = this.eval(ie.getLeft(), env);
				if (this.isError(left)) {
					return left;
				}
				Object_T index = this.eval(ie.getIndex(), env);
				if (this.isError(index)) {
					return index;
				}
				return this.evalIndexExpression(left, index);
			case FunctionLiteral fl:
				return this.addFunction(fl, env);
			case CallExpression ce:
				return this.evalCallOperation(ce, env);

			case DotExpression de:
				if (de.getRight() instanceof CallExpression) {

					CallExpression c = (CallExpression) de.getRight();
					c.addArgument(de.getLeft());
					return this.evalCallOperation(c, env);
				} else {
					return new Error_T("Wrong Type : " + de.getRight().getClass());
				}

			case IntegerLiteral i:
				return new Integer_T(i.getValue());
			case FloatLiteral f:
				return new Float_T(f.getValue());
			case BooleanLiteral b:
				return b.getValue() ? Constants.TRUE : Constants.FALSE;
			case StringLiteral s:
				return new String_T(s.getValue());
			case ArrayLiteral arr:
				Vector<Object_T> elements = this.evalExpressions(arr.getElements(), env);
				if (elements.size() == 1 && this.isError(elements.getFirst())) {
					return elements.getFirst();
				}
				return new Array_T(elements);
			case HashLiteral hl:
				return this.evalHashLiteral(hl, env);
			case Identifier id:
				return this.evalIdentifier(id, env);
			default:
				return Constants.NULL;
		}

	}

	Object_T evalProgram(Vector<Statement> statements, Environment env) {
		Object_T result = null;
		for (Statement stm : statements) {
			result = this.eval(stm, env);

			switch (result) {
				case Return_T rt:
					return rt.getValue();
				case Error_T et:
					return et;
				default:
					continue;
			}

		}
		return result;
	}

	Object_T evalCallOperation(CallExpression ce, Environment env) {
		Object_T func = this.eval(ce.getFunction(), env);
		if (this.isError(func)) {
			return func;
		}
		Vector<Object_T> args = this.evalExpressions(ce.getArguments(), env);
		if (args.size() == 1 && this.isError(args.get(0))) {
			return args.get(0);
		}
		return this.applyFunction(func, args);

	}

	Object_T evalLetStatement(LetStatement ls, Environment env) {
		if (env.isObjectPresent(ls.getName().getValue())) {
			return new Error_T(
					ls.getName().getValue() + " Exists. Cannot redeclare stuff.");
		}

		Object_T val = this.eval(ls.getValue(), env);
		if (this.isError(val)) {
			return val;
		}
		env.assignObject(ls.getName().getValue(), val);
		return val;

	}

	Object_T evalWhileStatement(WhileStatement ws, Environment env) {
		// int raceLimit = 100;
		// int loopCount = 0;

		Expression condn = ws.getCondition();
		Object_T condition = this.eval(condn, env);
		Object_T res = null;
		if (this.isError(condition))
			return condition;

		while (isTruth(condition)) {

			res = this.eval(ws.getBody(), env);
			if (res instanceof Return_T) {
				return ((Return_T) res).getValue();
			} else if (res instanceof Error_T) {
				return res;
			}
			// env.printObjects();
			condition = this.eval(condn, env);

		}
		return res != null ? res : new Null_T();
	}

	Object_T evalAssignmentStatement(AssignmentStatement as, Environment env) {

		if (!env.isObjectPresent(as.getName().getValue())) {
			return new Error_T(as.getName().getValue() + " Does not exist");
		}

		Object_T val = this.eval(as.getExpression(), env);
		if (this.isError(val)) {
			return val;
		}
		env.reAssignObject(as.getName().getValue(), val);
		return val;

	}

	Object_T evalBlockStatement(BlockStatement bs, Environment env) {
		Object_T result = null;
		Environment extendedEnv = new Environment(env);
		for (Statement stm : bs.getStatements()) {
			result = this.eval(stm, extendedEnv);

			if (result != null) {
				if (result instanceof Return_T || result instanceof Error_T) {
					return result;
				}
			}

		}
		return result;

	}

	Object_T evalPrefixExpression(String operator, Object_T right) {
		switch (operator) {
			case "!":
				return this.evalBangOperatorExpression(right);
			case "-":
				return this.evalMinusOperatorExpression(right);
			default:
				return new Error_T("Unknown Operator : " + operator + " " + right.type());
		}
	}

	Object_T evalInfixExpression(String operator, Object_T left, Object_T right) {
		if (left instanceof Integer_T && right instanceof Integer_T) {
			return this.evalIntegerInfixExpression(operator, left, right);

		} else if (left instanceof Float_T && right instanceof Float_T) {
			return this.evalFloatInfixExpression(operator, left, right);

		} else if (left instanceof String_T && right instanceof String_T) {
			return this.evalStringInfixExpression(operator, left, right);

		} else {
			if (left.type().equals(right.type()) && left instanceof Boolean_T) {
				switch (operator) {
					case "==":
						return this.nativeBoolToBooleanObject(((Boolean_T) left)
								.getValue() == ((Boolean_T) right).getValue());
					case "!=":
						return this.nativeBoolToBooleanObject(((Boolean_T) left)
								.getValue() != ((Boolean_T) right).getValue());
					default:
						return new Error_T(
								"Unknown operator : " + left.type() + " " + operator
										+ " " + right.type());

				}
			} else {
				return new Error_T(
						"Type mismatch : " + left.type() + " " + operator + " " + right.type());
			}
		}
	}

	Object_T evalIndexExpression(Object_T left, Object_T index) {
		if (left instanceof Array_T && index instanceof Integer_T) {
			return this.evalArrayIndexExpression((Array_T) left, (Integer_T) index);
		} else if (left instanceof Hash_T) {
			return evalHashIndexExpression((Hash_T) left, index);
		} else {
			return new Error_T("Index Operator not supported : " + left.type());
		}
	}

	Object_T evalArrayIndexExpression(Array_T left, Integer_T index) {
		int idx = index.getValue();
		int max = left.getValue().size() - 1;
		if (idx < 0 || idx > max) {
			return new Error_T("Array index out of range : " + idx);
		}
		return left.getValue().get(idx);

	}

	Object_T evalHashIndexExpression(Hash_T hash, Object_T index) {
		if (!(index instanceof Hashable)) {
			return new Error_T("Unusable as hash key : " + index.type());
		}
		HashPair pair = hash.getPairs().get(((Hashable) index).hash());
		if (pair == null) {
			System.out.println("aaaaaaaaaaaaahhhhhhhh");
			return new Null_T();
		}
		return pair.getValue();
	}

	Object_T evalHashLiteral(HashLiteral hl, Environment env) {
		Map<HashKey, HashPair> pairs = new HashMap<HashKey, HashPair>() {
			@Override
			public HashPair get(Object key) {
				Set<HashKey> keys = this.keySet();
				for (HashKey k : keys) {
					if (k.getKey() == ((HashKey) key).getKey()) {
						return super.get(k);
					}
				}
				return null;
			}
		};

		for (Expression keyExp : hl.getElements().keySet()) {
			Object_T key = this.eval(keyExp, env);
			if (this.isError(key)) {
				return key;
			}
			if (!(key instanceof Hashable)) {
				return new Error_T("Unusable as hash key : " + key.type());
			}
			Object_T value = this.eval(hl.getElement(keyExp), env);
			if (this.isError(value)) {
				return value;
			}

			pairs.put(((Hashable) key).hash(), new HashPair(key, value));

		}
		return new Hash_T(pairs);
	}

	Vector<Object_T> evalExpressions(Vector<Expression> args, Environment env) {
		Vector<Object_T> result = new Vector<Object_T>();
		for (Expression e : args) {
			Object_T evaluated = this.eval(e, env);
			result.addElement(evaluated);
			if (this.isError(evaluated)) {
				return result;
			}
		}
		return result;

	}

	Object_T evalStringInfixExpression(String operator, Object_T left, Object_T right) {

		if (!operator.equals("+")) {
			return new Error_T("Unknown Operator : " + left.type() + " " + operator + " " + right.type());
		}
		return new String_T(((String_T) left).getValue() + ((String_T) right).getValue());
	}

	Object_T evalIntegerInfixExpression(String operator, Object_T left, Object_T right) {
		int leftVal = ((Integer_T) left).getValue();
		int rightVal = ((Integer_T) right).getValue();
		switch (operator) {
			case "+":
				return new Integer_T(leftVal + rightVal);
			case "-":
				return new Integer_T(leftVal - rightVal);
			case "*":
				return new Integer_T(leftVal * rightVal);
			case "/":
				return new Integer_T(leftVal / rightVal);
			case "^":
				return new Integer_T((int) Math.pow(leftVal, rightVal));
			case "<":
				return nativeBoolToBooleanObject(leftVal < rightVal);
			case ">":
				return nativeBoolToBooleanObject(leftVal > rightVal);
			case "==":
				return nativeBoolToBooleanObject(leftVal == rightVal);
			case "!=":
				return nativeBoolToBooleanObject(leftVal != rightVal);
			case ">=":
				return nativeBoolToBooleanObject(leftVal >= rightVal);
			case "<=":
				return nativeBoolToBooleanObject(leftVal <= rightVal);
			default:
				return new Error_T(
						"Unknown operator : " + left.type() + " " + operator
								+ " " + right.type());
		}
	}

	Object_T evalFloatInfixExpression(String operator, Object_T left, Object_T right) {
		float leftVal = ((Float_T) left).getValue();
		float rightVal = ((Float_T) right).getValue();
		switch (operator) {
			case "+":
				return new Float_T(leftVal + rightVal);
			case "-":
				return new Float_T(leftVal - rightVal);
			case "*":
				return new Float_T(leftVal * rightVal);
			case "/":
				return new Float_T(leftVal / rightVal);
			case "^":
				return new Float_T((float) Math.pow(leftVal, rightVal));
			case "<":
				return nativeBoolToBooleanObject(leftVal < rightVal);
			case ">":
				return nativeBoolToBooleanObject(leftVal > rightVal);
			case "==":
				return nativeBoolToBooleanObject(leftVal == rightVal);
			case "!=":
				return nativeBoolToBooleanObject(leftVal != rightVal);
			case ">=":
				return nativeBoolToBooleanObject(leftVal >= rightVal);
			case "<=":
				return nativeBoolToBooleanObject(leftVal <= rightVal);
			default:
				return new Error_T(
						"Unknown operator : " + left.type() + " " + operator
								+ " " + right.type());
		}
	}

	Object_T evalBangOperatorExpression(Object_T right) {
		switch (right) {
			case Boolean_T bool:
				return !bool.getValue() ? Constants.TRUE : Constants.FALSE;
			case Null_T null_obj:
				return Constants.TRUE;
			default:
				return Constants.FALSE;
		}
	}

	Boolean_T nativeBoolToBooleanObject(boolean input) {
		return input ? Constants.TRUE : Constants.FALSE;
	}

	Object_T evalMinusOperatorExpression(Object_T right) {
		if (!(right instanceof Integer_T || right instanceof Float_T)) {
			return new Error_T("Unknown operator : " + right.type());
		}
		return (right instanceof Integer_T) ? new Integer_T(-(((Integer_T) right).getValue()))
				: new Float_T(-(((Float_T) right).getValue()));
	}

	Object_T evalIfExpression(IfExpression ie, Environment env) {
		Object_T condition = this.eval(ie.getCondition(), env);
		if (this.isError(condition))
			return condition;
		if (isTruth(condition)) {
			return this.eval(ie.getConsequence(), env);
		} else if (ie.getAlternative() != null) {
			return this.eval(ie.getAlternative(), env);
		} else {
			return Constants.NULL;
		}
	}

	Object_T evalIdentifier(Identifier id, Environment env) {

		if (env.isObjectPresent(id.getValue())) {
			return env.getObject(id.getValue());
		} else if (this.builtinFunctions.isFunctionPresent(id.getValue())) {
			return this.builtinFunctions.getBuiltIn(id.getValue());

		} else {
			return new Error_T("Identifier not found : " + id.getValue());
		}
	}

	Object_T addFunction(FunctionLiteral fl, Environment env) {
		Identifier name = fl.getName();

		if (!env.isObjectPresent(name.getNodeValue())) {
			Vector<Identifier> params = fl.getParameters();
			BlockStatement body = fl.getBody();
			Function_T function = new Function_T(name, params, env, body);

			env.assignObject(name.getNodeValue(), function);
			return function;
		} else {
			return new Error_T("Function already exists....");
		}

	}

	Object_T applyFunction(Object_T obj, Vector<Object_T> args) {
		switch (obj) {
			case Function_T function:

				Environment extendedEnv = this.extendEnv(function, args);
				Object_T evaluated = this.eval(function.getBody(), extendedEnv);
				return this.unWrapReturn(evaluated);
			case Builtin_Function_T bnf:
				return bnf.applyFunction(args);
			default:
				return new Error_T("Not a function : " + obj.type());
		}

	}

	Environment extendEnv(Function_T func, Vector<Object_T> args) {
		Environment env = new Environment(func.getEnv());

		for (int i = 0; i < func.getParameters().size(); i++) {
			Identifier param = func.getParameters().get(i);
			env.assignObject(param.getNodeValue(), args.get(i));
		}
		return env;
	}

	Object_T unWrapReturn(Object_T obj) {
		if (obj instanceof Return_T) {
			return ((Return_T) obj).getValue();
		}
		return obj;
	}

	boolean isTruth(Object_T condition) {
		if (condition instanceof Boolean_T) {
			return ((Boolean_T) condition).getValue();
		} else {
			return false;
		}
	}

	boolean isError(Object_T obj) {
		if (obj != null) {
			return obj instanceof Error_T;
		}
		return false;
	}

	public Vector<EvaluatorError> getErrors() {
		return this.errors;
	}

}
