package PRINTER;

import LEXER.Lexeme;

public class PrettyPrinter 
{	
	public void displayParseTree(Lexeme tree) 
	{
		displayStatements(tree);
	}
	
	public void displayParser(Lexeme tree) 
	{
		display(tree, "Root");
	}
	
	private void display(Lexeme tree, String s) 
	{
		System.out.println(s + ": " + tree.getType());
		if(tree.getLeft() != null)
			display(tree.getLeft(), "L");
		if(tree.getRight() != null)
			display(tree.getRight(), "R");
	}
	
	private void displayStatements(Lexeme lex) 
	{
		if(lex != null)
			displayStatement(lex);
		if(lex.getRight() != null)
			displayStatements(lex.getRight());
	}
	
	private void displayStatement(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		switch(lex.getType()) {
		case "EXPRESSION": displayExpression(lex);
			break;
		case "VARIABLE_DEFINE": displayVariableDefine(lex);
			break;
		case "FUNCTION": displayFunction(lex);
			break;
		case "ARRAY": displayArray(lex);
			break;
		case "CLASS": displayClass(lex);
			break;
		case "OBJECT": displayObject(lex);
			break;
		case "CONDITIONAL": displayConditional(lex);
			break;
		case "INCLUDE": displayInclude(lex);
			break;
		case "COMMENT": displayComment(lex);
			break;
		}
		System.out.println();
	}
	
	private void displayExpression(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		if(lex != null) {
			displayUnary(lex);
			if(tag.getRight() != null) {
				System.out.print(" ");
				displayExpression(tag.getRight());
			}
		}
	}
	
	private void displayVariableDefine(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		System.out.print("max ");
		displayVariable(lex);
		displayInitializerExpression(tag.getRight());
	}
	
	private void displayFunction(Lexeme lex) 
	{
		System.out.print("maximus ");
		displayVariable(lex.getLeft());
		displayParameterExpression(lex.getLeft().getLeft());
		displayCode(lex.getRight());
	}
	
	private void displayArray(Lexeme lex) 
	{
		System.out.print("array ");
		displayVariable(lex.getLeft());
		displayInitializerExpression(lex.getRight());
	}
	
	private void displayClass(Lexeme lex) 
	{
		System.out.print("class ");
		displayVariable(lex.getLeft());
		displayCode(lex.getRight());
	}
	
	private void displayObject(Lexeme lex) 
	{
		System.out.print("object ");
		displayVariable(lex.getLeft());
		displayInitializerExpression(lex.getRight());
	}
	
	private void displayConditional(Lexeme lex) 
	{
		switch(lex.getLeft().getType())
		{
		case "IF": displayIf(lex.getLeft());
			break;
		case "WHILE": displayWhile(lex.getLeft());
			break;
		}
	}
	
	private void displayInclude(Lexeme lex) 
	{
		System.out.print("include ");
		System.out.print("\"" + lex.getLeft().getValue() + "\"");
	}
	
	private void displayComment(Lexeme lex) 
	{
		System.out.print("~");
		System.out.print(lex.getValue());
		System.out.print("~");
	}
	
	private void displayUnary(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		switch(lex.getType()) 
		{
		case "STRING": System.out.print("\"" + lex.getValue() + "\"");
				break;
		case "DOUBLE": System.out.print(lex.getValue());
				break;
		case "INTEGER": System.out.print(lex.getValue());
				break;
		case "OBJECT_EXPRESSION": displayObjectExpression(lex);
				break;
		case "ANONYMOUS_EXPRESSION": displayAnonymous(lex);
				break;	
		default: 
				displayVariableExpression(lex);
		}
	}
	
	private void displayInitializerExpression(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		System.out.print("(");
		if(lex != null)
			displayExpression(lex);
		System.out.print(")");
	}
	
	private void displayVariable(Lexeme lex) 
	{
		System.out.print(lex.getValue());
	}
	
	private void displayParameterExpression(Lexeme lex) 
	{
		System.out.print("(");
		if(lex.getLeft() != null)
			displayOptParameterList(lex.getLeft());
		System.out.print(") ");
	}
	
	private void displayIf(Lexeme lex)
	{
		System.out.print("if ");
		System.out.print("(");
		displayExpression(lex.getLeft());
		System.out.print(") ");
		displayCode(lex.getRight().getLeft());
		if(lex.getRight().getRight() != null)
			displayOptElse(lex.getRight().getRight());
	}
	
	private void displayWhile(Lexeme lex) 
	{
		System.out.print("while ");
		System.out.print("(");
		displayExpression(lex.getLeft());
		System.out.print(") ");
		displayCode(lex.getRight());
	}
	
	private void displayAnonymous(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		if(lex.getRight().check("CALL")) 
			displayAnonymousCall(lex);
		else
			displayAnonymousDefine(lex);
	}
	
	private void displayObjectExpression(Lexeme tag) 
	{
		displayVariable(tag.getLeft());
		Lexeme lex = tag.getLeft();
		while(lex.getLeft() != null) 
		{
			System.out.print(".");
			displayVariable(lex.getLeft());
			lex = lex.getLeft();
		}
		if(tag.getRight() != null) 
		{
			if(tag.getRight().check("OBJECT_EXPRESSION"))
				displayObjectExpression(tag.getRight());
			else if(tag.getRight().check("ASSIGN")) 
			{
				System.out.print(" = ");
				displayUnary(tag.getRight().getLeft());
			}
			else if(tag.getRight().check("INITIALIZER_EXPRESSION"))
				displayInitializerExpression(tag.getRight());
		}
	}
	
	private void displayVariableExpression(Lexeme tag) 
	{
		displayVariable(tag);
		if(tag.getLeft() != null) {
			if(tag.getLeft().check("ASSIGN")) 
			{
				System.out.print(" = ");
				displayUnary(tag.getLeft().getLeft());
			}
			else displayInitializerExpression(tag.getLeft());
		}
	}
	
	private void displayCode(Lexeme lex) 
	{
		System.out.println("{");
		if(lex.getLeft() != null) 
		{
			displayStatements(lex.getLeft());
		}
		System.out.print("}");
	}
	
	private void displayOptParameterList(Lexeme tag) 
	{
		Lexeme lex = tag.getLeft();
		if(lex != null) {
			if(tag.getRight() == null)
				System.out.print(lex.getValue());
			else {
				System.out.print(lex.getValue() + " ");
				displayOptParameterList(tag.getRight());
			}
		}
	}
	
	private void displayOptElse(Lexeme lex) 
	{
		System.out.print("else ");
		if(lex.getLeft().check("IF")) 
		{
			displayIf(lex.getLeft());
		}
		else
			displayCode(lex.getLeft());
	}
	
	private void displayAnonymousDefine(Lexeme lex) 
	{
		System.out.print("mystery ");
		displayParameterExpression(lex.getLeft());
		displayCode(lex.getRight());
	}
	
	private void displayAnonymousCall(Lexeme lex) 
	{
		System.out.print("(");
		System.out.print("mystery ");
		displayParameterExpression(lex.getLeft());
		displayCode(lex.getRight().getLeft());
		displayInitializerExpression(lex.getRight().getRight());
		System.out.print(")");
	}
}
