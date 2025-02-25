package org.evaluator;

import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.typesystem.*;
import org.parser.literals.*;

import java.util.Vector;

public class Evaluator {

	public Object_T eval(Node node) {
		switch (node) {
			// NOTE: Statement Evaluation
			case Program p:
				return this.evalStatements(p.getStatements());
			case ExpressionStatement e:
				return this.eval(e.getExpression());
			case BlockStatement bs:
				return evalStatements(bs.getStatements());
			case IfExpression ie:
				return evalIfExpression(ie);

			// NOTE: Expression Evaluation
			case PrefixExpression pe:
				Object_T right = this.eval(pe.getRight());
				return this.evalPrefixExpression(pe.getOperator(), right);
			case InfixExpression ie:
				Object_T left = this.eval(ie.getLeft());
				right = this.eval(ie.getRight());
				return evalInfixExpression(ie.getOperator(), left, right);
			case IntegerLiteral i:
				return new Integer_T(i.getValue());
			case BooleanLiteral b:
				return b.getValue() ? Constants.TRUE : Constants.FALSE;
			default:
				return Constants.NULL;
		}

	}

	Object_T evalStatements(Vector<Statement> statements) {
		Object_T result = null;
		for (Statement stm : statements) {
			result = eval(stm);
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
				return Constants.NULL;
		}
	}

	Object_T evalInfixExpression(String operator, Object_T left, Object_T right) {
		if (left instanceof Integer_T && right instanceof Integer_T) {
			return evalIntegerInfixExpression(operator, left, right);

		} else {
			switch (operator) {
				case "==":
					return nativeBoolToBooleanObject(left == right);
				case "!=":
					return nativeBoolToBooleanObject(left != right);
				default:
					return Constants.NULL;
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
				return Constants.NULL;
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
			return Constants.NULL;
		}
		return new Integer_T(-(((Integer_T) right).getValue()));
	}

	Object_T evalIfExpression(IfExpression ie) {
		Object_T condition = eval(ie.getCondition());
		if (isTruth(condition)) {
			return eval(ie.getConsequence());
		} else if (ie.getAlternative() != null) {
			return eval(ie.getAlternative());
		} else {
			return Constants.NULL;
		}
	}

	boolean isTruth(Object_T condition) {
		if (condition instanceof Boolean_T) {
			return ((Boolean_T) condition).getValue();
		} else {
			return false;
		}
	}

}
