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
		String prompt = ">> ";
		System.out.println("Welcome to the Crazy Programming Language...\nEnter the commands below");
		while (true) {
			System.out.print(prompt);
			String input = sc.nextLine();
			if (input == null || input.equals("")) {
				continue;
			}
			Lexer lx = new Lexer(input);
			lx.tokenize();
			Vector<Token> tokens = lx.getTokens();
			for (Token tok : tokens) {
				tok.printToken();
			}

			System.out.println("\n\n\n\n----------Parsing------------\n\n\n");
			Parser ps = new Parser(tokens);
			ps.parseProgram();
			Vector<ParserError> errors = ps.getErrors();
			for (ParserError er : errors) {
				er.printError();
			}
		}

	}
}
