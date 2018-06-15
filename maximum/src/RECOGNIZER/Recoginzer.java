package recognizer;

import lexer.Lexer;
import io.Fatal;


public class Recognizer {

	private Lexer lex;
	
	public Recognizer(String fileName) 
	{
		lex = new Lexer(fileName);
	}
	public void run() {
		while(!check("ENDofINPUT")) 
		{
			statements();
		}
		System.out.println("Recognizer parsed file successfully.");
	}
	public void statements() 
	{
		statement();
		if(lex.statementPending()) 
		{
			statements();
		}
	}
	public void statement() 
	{
		if(check("NEWLINE")) match("NEWLINE");
		else if(lex.expressionPending()) expression();
		else if(lex.variableDefPending()) variableDef();
		else if(lex.functionDefPending()) functionDef();
		else if(lex.arrayDefPending()) arrayDef();
		else if(lex.classDefPending()) classDef();
		else if(lex.objectDefPending()) objectDef();
		else if(lex.conditionalPending()) conditional();
		else if(check("INCLUDE")) include();
		else
			comment();
	}
	public void expression() 
	{
		unary();
			if(lex.expressionPending()) expression();
	}
	public void variableDef() 
	{
		match("VARIABLE_DEFINE");
		match("VARIABLE");
		initExpression();	
	}
	public void functionDef() 
	{
		match("FUNCTION");
		match("VARIABLE");
		parameterExpression();
		block();
	}
	public void arrayDef() 
	{
		match("ARRAY");
		match("VARIABLE");
		initExpression();
	}
	public void classDef() 
	{
		match("CLASS");
		match("VARIABLE");
		block();
	}
	public void objectDef() 
	{
		match("OBJECT");
		match("VARIABLE");
		initExpression();
	}
	public void conditional() 
	{
		if(lex.ifPending()) ifStatement();
		else whileLoop();	
	}
	public void include() 
	{
		match("INCLUDE");
		match("STRING");
	}
	public void comment() 
	{
		match("COMMENT");
	}
	public void unary() 
	{
		if(check("INTEGER")) match("INTEGER");
		else if(check("REAL")) match("REAL");
		else if(check("STRING")) match("STRING");
		else if(check("ASSIGN")) match ("ASSIGN");
		else if(lex.anonymousPending()) anonymousExpression();
		else 
		{
			match("VARIABLE");
			if(lex.variableExpressionPending()) 
			{
				variableExpression();
			}
		}
	}
	public void initExpression() 
	{
		match("OPEN_PAREN");
		expression();
		match("CLOSE_PAREN");
	}
	public void parameterExpression() 
	{
		match("OPEN_PAREN");
		optParameterList();
		match("CLOSE_PAREN");
	}
	public void block() 
	{
		match("OPEN_BRACE");
		if(lex.statementPending())
			statements();
		match("CLOSE_BRACE");
		if(check("NEWLINE"))
			match("NEWLINE");
	}
	public void ifStatement() 
	{
		match("IF");
		match("OPEN_PAREN");
		expression();
		match("CLOSE_PAREN");
		block();
		optElse();
	}
	public void whileLoop() 
	{
		match("WHILE");
		match("OPEN_PAREN");
		expression();
		match("CLOSE_PAREN");
		block();
	}
	public void anonymousExpression() 
	{
		if(lex.getCurrentLexeme().check("OPEN_PAREN"))
			anonCall();
		else
			anonDefine();
	}
	public void variableExpression() 
	{
		if(check("DOT")) 
		{
			match("DOT");
			match("VARIABLE");
		}
		if(lex.initExpressionPend()) 
		{
				initExpression();
		}
	}
	public void objectExpression() 
	{
		match("VARIABLE");
		if(lex.initExpressionPend()) 
		{
			initExpression();
		}
	}
	public void optParameterList() 
	{
		if(check("VARIABLE")) {
			match("VARIABLE");
			optParameterList();
		}
	}
	public void optElse() 
	{
		if(check("ELSE")) 
		{
			match("ELSE");
			if(lex.ifPending()) ifStatement();
			else block();
		}
	}
	private boolean check(String string) 
	{
		return lex.getCurrentLexeme().check(string);
	}
	private void match(String type) 
	{
		if(!check(type)) 
		{
			Fatal.FATAL("Expected " + type, lex.getCurrentLexeme().getLine());
		}
		lex.advance();
	}	
	public void anonCall() 
	{
		match("OPEN_PAREN");
		match("ANONYMOUS");
		parameterExpression();
		block();
		initExpression();
		match("CLOSE_PAREN");
	}
	public void anonDefine() 
	{
		match("ANONYMOUS");
		parameterExpression();
		block();
	}
}