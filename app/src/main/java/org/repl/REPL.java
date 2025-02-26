package org.repl;

import org.lexer.*;
import org.parser.*;
import org.typesystem.Object_T;
import org.environment.Environment;
import org.error.ParserError;
import org.evaluator.Evaluator;

import java.util.Scanner;
import java.util.Vector;

class REPL {

	public static void main(String[] args) {

		try {
			Scanner sc = new Scanner(System.in);
			boolean debug = true;
			if (args.length > 0) {
				System.out.println(args[0].equals("true"));
				debug = args[0].equals("true");
			}
			String prompt = ">> ";
			System.out.println(
					"Welcome to JorkLang where you can jork the lang...\nEnter the commands below");

			Environment env = new Environment();
			Evaluator evaluator = new Evaluator(env);
			while (true && env != null) {
				System.out.print(prompt);
				String input = sc.nextLine();
				if (input == null || input.equals("")) {
					continue;
				}
				if (input.equals("exit")) {
					sc.close();
					break;
				}
				Lexer lx = new Lexer(input, debug);
				lx.tokenize();
				lx.printTokens();
				Vector<Token> tokens = lx.getTokens();
				if (debug)
					System.out.println("\n\n\n\n----------Parsing------------\n\n\n");
				Parser ps = new Parser(tokens, debug);
				ps.parseProgram();
				ps.printProgram();
				Program program = ps.getProgram();
				Vector<ParserError> errors = ps.getErrors();
				if (errors.size() == 0) {
					Object_T object = evaluator.eval(program);
					// System.out.println(object);

					if (object != null) {
						System.out.println(object.inspect());
					} else {
						System.out.println("Object is null");
					}
				}

				for (ParserError er : errors) {
					er.printError();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
