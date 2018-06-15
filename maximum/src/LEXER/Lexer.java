package LEXER;

import java.io.EOFException;

import INPUTOUTPUT.FileInput;

public class Lexer {

	private boolean eof;
	private Integer line;
	Lexeme lex;
	FileInput file;

	public Lexer(String fileName) {
		file = new FileInput(fileName);
		eof = false;
		line = 1;
		advance();
	}
	public Lexeme getLex() {
		return lex;
	}
	public void setLex(Lexeme currentLexeme) {
		this.lex = lex;
	}
	public void scanner() {
		lex.display();
		while(lex.type != "ENDofINPUT") {
			advance();
			lex.display();
		}
	}
	public void advance() {
		if(eof)
			lex = new Lexeme("ENDofINPUT", line);
		else
			try {
				lex = lex();
			} catch (EOFException e) {
				lex = new Lexeme("ENDofINPUT", line);

			}
	}
	public boolean statementPending() {
		return expressionPending() || variableDefPending() || functionDefPending()
				|| arrayDefPending() || classDefPending() || objectDefPending() 
				|| conditionalPending() || lex.check("INCLUDE") || commentPending() 
				|| lex.check("NEWLINE");
	}
	public boolean conditionalPending() {
		return ifPending() || whilePending();
	}
	
	public boolean classDefPending() {
		return lex.check("CLASS");
	}
	public boolean whilePending() {
		return lex.check("WHILE");
	}	
	public boolean objectDefPending() {
		return lex.check("OBJECT");
	}
	public boolean commentPending() {
		return lex.check("COMMENT");
	}
	public boolean unaryPending() {
		return lex.check("INTEGER") || lex.check("REAL") 
				|| lex.check("STRING") || lex.check("ASSIGN") 
				|| lex.check("VARIABLE") || anonymousPending();
	}
	public boolean initializerExpressionPending() {
		return lex.check("OPEN_PAREN");
	}
	public boolean anonymousPending() {
		return lex.check("ANONYMOUS") || lex.check("OPEN_PAREN"); 
	}
	public boolean objectExpressionPending() {
			return lex.check("DOT");
	}
	public boolean variableDefPending() {
		return lex.check("VARIABLE_DEFINE");
	}
	public boolean functionDefPending() {
		return lex.check("FUNCTION");
	}
	public boolean arrayDefPending() {
		return lex.check("ARRAY");
	}
	public boolean expressionPending() {
		return unaryPending();
	}
	public boolean variableExpressionPending() {
		return lex.check("VARIABLE");
	}
	public boolean ifPending() {
		return lex.check("IF");
	}
	
	public boolean optParameterListPending() {
		return lex.check("VARIABLE");
	}
	private Lexeme lexComment() throws EOFException {
		Character c;
		String token = new String();
		c = file.readNextRawCharacter();
		while(c != '~' && !eof) {
			token = token.concat(Character.toString(c));
			try {
				c = file.readNextRawCharacter();
			} catch (EOFException e) {
				eof = true;
			}
		}
		return new Lexeme("COMMENT", token, line);
	}
	public Lexeme lex() throws EOFException {
		Character c;
		file.skipWhitespace();
		c = file.readNextRawCharacter();
		switch(c) 
		{
		case '{': 	return new Lexeme("OPEN_BRACE", line);
		case '}':	return new Lexeme("CLOSE_BRACE", line);
		case '(':	return new Lexeme("OPEN_PAREN", line);
		case ')': 	return new Lexeme("CLOSE_PAREN", line);
		case '=':	return new Lexeme("ASSIGN", line);
		case '.': 	return new Lexeme("DOT", line);
		case '~': 	return lexComment();
		case '\n':	{
			line += 1;
			return new Lexeme("NEWLINE", line - 1);
		}
		default:
			if(Character.isDigit(c) || c == '-') {
				file.pushbackCharacter(c);
				return lexNumber();
			}
			else if(Character.isLetter(c)) {
				file.pushbackCharacter(c);
				return lexVarOrKeyword();
			}
			else if(c == '\"') {
				return lexString();
			}
			else {
				return new Lexeme("UNKNOWN", line);
			}
		}
	}	
	private Lexeme lexVarOrKeyword() throws EOFException {
		Character c;
		String token = new String();
		c = file.readNextRawCharacter();
		while(Character.isLetterOrDigit(c) && !eof) {
			token = token.concat(Character.toString(c));
			try {
				c = file.readNextRawCharacter();
			} catch (EOFException e) {
				eof = true;
			}
		}
		file.pushbackCharacter(c);
		if(token.equals("maximus")) return new Lexeme("FUNCTION", line);
		else if(token.equals("max")) return new Lexeme("VARIABLE_DEFINE", line);
		else if(token.equals("if")) return new Lexeme("IF", line);
		else if(token.equals("else")) return new Lexeme("ELSE", line);
		else if(token.equals("while")) return new Lexeme("WHILE", line);
		else if(token.equals("array")) return new Lexeme("ARRAY", line);
		else if(token.equals("class"))	return new Lexeme("CLASS", line);
		else if(token.equals("object")) return new Lexeme("OBJECT", line);
		else if(token.equals("include")) return new Lexeme("INCLUDE", line);
		else if(token.equals("mystery")) return new Lexeme("ANONYMOUS", line);
		else return new Lexeme("VARIABLE", token, line);	
	}
	private Lexeme lexString() throws EOFException {
		Character c;
		String token = new String();
		c = file.readNextRawCharacter();
		while(c != '\"' && !eof) {
			token = token.concat(Character.toString(c));
			try {
				c = file.readNextRawCharacter();
			} catch (EOFException e) {
				eof = true;
			}
		}
		return new Lexeme("STRING", token, line);
	}
	private Lexeme lexNumber() throws EOFException {
		Character c;
		String token = new String();
		c = file.readNextRawCharacter();
		while((Character.isDigit(c) || c == '.' || c == '-') && !eof) {
			token = token.concat(Character.toString(c));
			try {
				c = file.readNextRawCharacter();
			} catch (EOFException e) {
				eof = true;
			}
		}
		file.pushbackCharacter(c);
		if(token.contains(Character.toString('.'))) {
			try {
				return new Lexeme("REAL", Double.parseDouble(token), line);
			} catch(NumberFormatException e) {
				return new Lexeme("UNKNOWN", line);
			}
		}
		else {
			try {
				return new Lexeme("INTEGER", Integer.parseInt(token), line);
			} catch(NumberFormatException e) {
				return new Lexeme("UNKNOWN", line);
			}
		}
	}
	
}