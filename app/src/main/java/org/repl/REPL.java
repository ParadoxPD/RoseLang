package org.repl;

import org.lexer.Lexer;
import org.lexer.Token;
import org.parser.Parser;

import org.error.ParserError;

import java.util.Scanner;
import java.util.Vector;

class REPL {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println(args[0].equals("true"));
		boolean debug = args[0].equals("true");
		String prompt = ">> ";
		System.out.println("Welcome to JorkLang where you can jork the lang...\nEnter the commands below");
		while (true) {
			System.out.print(prompt);
			String input = sc.nextLine();
			if (input == null || input.equals("")) {
				continue;
			}
			Lexer lx = new Lexer(input, debug);
			lx.tokenize();
			lx.printTokens();
			Vector<Token> tokens = lx.getTokens();
			System.out.println("\n\n\n\n----------Parsing------------\n\n\n");
			Parser ps = new Parser(tokens, debug);
			ps.parseProgram();
			ps.printProgram();
			Vector<ParserError> errors = ps.getErrors();
			for (ParserError er : errors) {
				er.printError();
			}
		}

	}
}
