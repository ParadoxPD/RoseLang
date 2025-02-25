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
				return this.evalStatement(p.getStatements());
			case ExpressionStatement e:
				return this.eval(e.getExpression());

			// NOTE: Expression Evaluation
			case PrefixExpression pf:
				Object_T right = this.eval(pf.getRight());
				return this.evalPrefixExpression(pf.getOperator(), right);
			case IntegerLiteral i:
				return new Integer_T(i.getValue());
			case BooleanLiteral b:
				return b.getValue() ? Constants.TRUE : Constants.FALSE;
			default:
				return Constants.NULL;
		}

	}

	Object_T evalStatement(Vector<Statement> statements) {
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

	Object_T evalMinusOperatorExpression(Object_T right) {
		if (!(right instanceof Integer_T)) {
			return Constants.NULL;
		}
		return new Integer_T(-(((Integer_T) right).getValue()));
	}

}
