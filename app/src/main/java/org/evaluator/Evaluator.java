package org.evaluator;

import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.typesystem.*;
import org.parser.literals.*;
import org.environment.*;
import org.error.*;

import java.util.Vector;

public class Evaluator {
	private Vector<EvaluatorError> errors;
	private Environment env;

	public Evaluator(Environment env) {
		this.env = env;
		this.errors = new Vector<EvaluatorError>();
	}

	public Object_T eval(Node node) {
		switch (node) {
			// NOTE: Statement Evaluation
			case Program p:
				return this.evalProgram(p.getStatements());
			case ExpressionStatement e:
				return this.eval(e.getExpression());
			case BlockStatement bs:
				return evalBlockStatement(bs);
			case IfExpression ie:
				return evalIfExpression(ie);
			case ReturnStatement rs:
				Object_T val = this.eval(rs.getReturnValue());
				if (this.isError(val))
					return val;
				return new Return_T(val);
			case LetStatement ls:
				val = this.eval(ls.getValue());
				if (this.isError(val)) {
					return val;
				}
				this.env.setObject(ls.getName().getValue(), val);
				return val;

			// NOTE: Expression Evaluation
			case PrefixExpression pe:
				Object_T right = this.eval(pe.getRight());
				if (this.isError(right))
					return right;
				return this.evalPrefixExpression(pe.getOperator(), right);
			case InfixExpression ie:
				Object_T left = this.eval(ie.getLeft());
				if (this.isError(left))
					return left;
				right = this.eval(ie.getRight());
				if (this.isError(right))
					return right;
				return evalInfixExpression(ie.getOperator(), left, right);
			case IntegerLiteral i:
				return new Integer_T(i.getValue());
			case BooleanLiteral b:
				return b.getValue() ? Constants.TRUE : Constants.FALSE;
			case Identifier id:
				return evalIdentifier(id);
			default:
				return Constants.NULL;
		}

	}

	Object_T evalProgram(Vector<Statement> statements) {
		Object_T result = null;
		for (Statement stm : statements) {
			result = this.eval(stm);

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

	Object_T evalBlockStatement(BlockStatement bs) {
		Object_T result = null;
		for (Statement stm : bs.getStatements()) {
			result = this.eval(stm);

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
			return evalIntegerInfixExpression(operator, left, right);

		} else {
			if (left.type().equals(right.type())) {
				switch (operator) {
					case "==":
						return nativeBoolToBooleanObject(left == right);
					case "!=":
						return nativeBoolToBooleanObject(left != right);
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
			case "<":
				return nativeBoolToBooleanObject(leftVal < rightVal);
			case ">":
				return nativeBoolToBooleanObject(leftVal > rightVal);
			case "==":
				return nativeBoolToBooleanObject(leftVal == rightVal);
			case "!=":
				return nativeBoolToBooleanObject(leftVal != rightVal);
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
		if (!(right instanceof Integer_T)) {
			return new Error_T("Unknown operator : " + right.type());
		}
		return new Integer_T(-(((Integer_T) right).getValue()));
	}

	Object_T evalIfExpression(IfExpression ie) {
		Object_T condition = eval(ie.getCondition());
		if (this.isError(condition))
			return condition;
		if (isTruth(condition)) {
			return eval(ie.getConsequence());
		} else if (ie.getAlternative() != null) {
			return eval(ie.getAlternative());
		} else {
			return Constants.NULL;
		}
	}

	Object_T evalIdentifier(Identifier id) {

		if (this.env.isObjectPresent(id.getValue())) {
			return this.env.getObject(id.getValue());
		} else {
			return new Error_T("Identifier not found : " + id.getValue());
		}
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
