package PARSER;

import java.util.ArrayList;

import INPUTOUTPUT.Fatal;
import LEXER.Lexeme;
import LEXER.Lexer;
import PRINTER.PrettyPrinter;

public class Parser {
	private Lexeme root;
	private Lexer l;
	
	
	public Parser(String fileName) {
		l = new Lexer(fileName);
	}
	
	public Lexeme run() 
	{
		root = statements();
		return root;
	}
	
	public Lexeme getConditional(Lexeme plague)
	{
		return plague.getLeft();
	}
	
	public Lexeme getIfExpression(Lexeme plague) 
	{
		return plague.getLeft().getLeft();
	}
	
	public boolean hasElse(Lexeme plague) 
	{
		return plague.getRight().getRight() != null;
	}
	
	public boolean hasElseIf(Lexeme plague) 
	{
		return plague.getRight().getRight().getLeft().check("IF");
	}
	
	public Lexeme getElseIfExpression(Lexeme plague) 
	{
		return plague.getRight().getRight().getLeft();
	}
	
	public Lexeme getElseExpression(Lexeme plague) 
	{
		return plague.getRight().getRight().getLeft().getLeft();
	}
	
	public Lexeme getIfBlock(Lexeme plague) 
	{
		return plague.getRight().getLeft().getLeft();
	}
	
	public Lexeme getIfOptElse(Lexeme plague) 
	{
		return plague.getRight().getRight();
	}
	
	public Lexeme getWhileExprssion(Lexeme plague) 
	{
		return plague.getLeft().getLeft();
	}
	public Lexeme getStatement(Lexeme plague) 
	{
		return plague.getLeft();
	}
		
	public Lexeme getParams(Lexeme plague) 
	{
		return plague.getLeft().getLeft().getLeft();
	}
	
	public Lexeme getInclude(Lexeme plague) 
	{
		return plague.getLeft();
	}
	
	public Lexeme getWhileBlock(Lexeme plague) 
	{
		return plague.getRight().getLeft();
	}
	
	public Lexeme getFirstExpression(Lexeme plague) 
	{
		return plague.getLeft().getLeft().getLeft();
	}
	
	public Lexeme getSecondExpression(Lexeme plague) 
	{
		return plague.getLeft().getLeft().getRight().getLeft();
	}
	
	public Lexeme getVariable(Lexeme plague) 
	{
		return plague.getLeft();
	}
	
	public Lexeme getBlock(Lexeme plague) 
	{
		return plague.getRight();
	}
	
	public Lexeme getClassName(Lexeme plague) 
	{
		return plague.getRight().getLeft().getLeft().getLeft();
	}
	
	public Lexeme getAnonymousParameters(Lexeme plague) 
	{
		return plague.getLeft().getLeft();
	}
	
	public Lexeme getAnonymousBlock(Lexeme plague) 
	{
		return plague.getRight().getLeft().getLeft();
	}
	
	public ArrayList<Lexeme> getArrayInitilizers(Lexeme plague) 
	{
		ArrayList<Lexeme> lex = new ArrayList<Lexeme>();
		Lexeme l = plague.getRight().getLeft();
		while(l != null) {
			lex.add(l.getLeft());
			l = l.getRight();
		}
		return lex;
	}
	
	public ArrayList<Lexeme> getFunctionInitializers(Lexeme plague) 
	{
		ArrayList<Lexeme> lex = new ArrayList<Lexeme>();
		Lexeme l = plague.getLeft().getLeft();
		while(l != null) {
			lex.add(l.getLeft());
			l = l.getRight();

		}
		return lex;
	}
	
	public ArrayList<Lexeme> getObjectExpressionInitializers(Lexeme plague) 
	{
		ArrayList<Lexeme> lex = new ArrayList<Lexeme>();
		Lexeme l = plague.getRight().getLeft();
		while(l != null) {
			lex.add(l.getLeft());
			l = l.getRight();

		}
		return lex;
	}
	
	public ArrayList<Lexeme> getObjectDefineInitializers(Lexeme plague) 
	{
		ArrayList<Lexeme> lex = new ArrayList<Lexeme>();
		Lexeme l = plague.getRight().getLeft().getLeft().getLeft().getLeft().getLeft();
		while(l != null) {
			lex.add(l.getLeft());
			l = l.getRight();

		}
		return lex;
	}
	public boolean variableExpressionLookup(Lexeme plague) 
	{
		return plague.getLeft() == null;
	}
	public ArrayList<Lexeme> getAnonymousInitilizers(Lexeme plague) 
	{
		ArrayList<Lexeme> lex = new ArrayList<Lexeme>();
		Lexeme l = plague.getRight().getRight().getLeft();
		while(l != null) {
			lex.add(l.getLeft());
			l = l.getRight();
		}
		return lex;
	}
	public Lexeme  getUnary(Lexeme plague) 
	{
		return plague.getLeft();
	}
	public Lexeme getInitilizer(Lexeme plague) 
	{
		if(plague.getRight().getLeft() == null)
			return null;
		else
			return plague.getRight().getLeft().getLeft();
	}
	public Lexeme getObject(Lexeme plague) 
	{
		return plague.getLeft();
	}
	public boolean anonymousHasCall(Lexeme plague) 
	{
		return plague.getLeft().getRight().check("CALL");
	}
	public Lexeme getVariableExpression(Lexeme plague) 
	{
		return plague.getLeft();
	}
	public Lexeme getObjectAssignInitilizers(Lexeme plague) 
	{
		return plague.getRight().getLeft();
	}
	
	private Lexeme statements() 
	{
		Lexeme plague = statement();
		while(plague == null && l.statementPending()) {
			plague = statement();
		}
		if(l.statementPending()) {
			plague.setRight(statements());
		}
		return plague;
	}
	
	private Lexeme statement() 
	{
		Lexeme plague = null;
		if(check("NEWLINE")) 
		{
			match("NEWLINE");
		}
		else if(check("COMMENT")) 
		{
			match("COMMENT");
		}
		else 
		{
			plague = new Lexeme("STATEMENT", l.getLex().getLine());
			if(l.expressionPending()) plague.setLeft(expression());
			else if(l.variableDefPending()) plague.setLeft(variableDef());
			else if(l.functionDefPending()) plague.setLeft(functionDef());	
			else if(l.arrayDefPending()) plague.setLeft(arrayDef());
			else if(l.classDefPending()) plague.setLeft(classDef());
			else if(l.objectDefPending()) plague.setLeft(objectDef());
			else if(l.conditionalPending()) plague.setLeft(conditional());
			else if(check("INCLUDE")) plague.setLeft(include());
		}
		return plague;
	}
	
	private Lexeme functionDef() 
	{
		Lexeme plague = match("FUNCTION");
		plague.setLeft(match("VARIABLE"));
		plague.getLeft().setLeft(parameterExpression());
		plague.setRight(block());
		return plague;
	}
	
	private Lexeme arrayDef() 
	{
		Lexeme plague = match("ARRAY");
		plague.setLeft(match("VARIABLE"));
		plague.setRight(initializerExpression());
		return plague;
	}
	private Lexeme expression() 
	{
		Lexeme plague = new Lexeme("EXPRESSION", l.getLex().getLine());
		plague.setLeft(unary());
		if(l.expressionPending()) 
		{
			plague.setRight(expression());
		}
		return plague;
	}
	private Lexeme variableDef() 
	{
		Lexeme plague = match("VARIABLE_DEFINE");
		plague.setLeft(match("VARIABLE"));
		plague.setRight(initializerExpression());
		return plague;
	}
	private Lexeme classDef() 
	{
		Lexeme plague = match("CLASS");
		plague.setLeft(match("VARIABLE"));
		plague.setRight(block());
		return plague;
	}
	private Lexeme objectDef() 
	{
		Lexeme plague = match("OBJECT");
		plague.setLeft(match("VARIABLE"));
		plague.setRight(initializerExpression());
		return plague;
	}
	private Lexeme conditional() 
	{
		Lexeme plague = new Lexeme("CONDITIONAL", l.getLex().getLine());
		if(l.ifPending()) {
			plague.setLeft(ifStatement());
		}
		else plague.setLeft(whileLoop());	
		return plague;
	}
	private Lexeme include() 
	{
		Lexeme plague = match("INCLUDE");
		plague.setLeft(match("STRING"));
		return plague;
	}
	private Lexeme unary() 
	{
		Lexeme plague = new Lexeme("UNARY", l.getLex().getLine());
		if(check("INTEGER")) plague.setLeft(match("INTEGER"));
		else if(check("REAL"))	plague.setLeft(match("REAL"));
		else if(check("STRING")) plague.setLeft(match("STRING"));
		else if(l.anonymousPending()) plague.setLeft(anonymousExpression());
		else plague.setLeft(varExpr());
		return plague;
	}
	private Lexeme initializerExpression() 
	{
		Lexeme plague = new Lexeme("INITIALIZER_EXPRESSION", l.getLex().getLine());
		match("OPEN_PAREN");
		if(l.unaryPending())
			plague.setLeft(expression());
		match("CLOSE_PAREN");
		return plague;
	}
	private Lexeme block() 
	{
		Lexeme plague = new Lexeme("BLOCK", l.getLex().getLine());
		match("OPEN_BRACE");
		if(l.statementPending()) {
			plague.setLeft(statements());
		}
		match("CLOSE_BRACE");
		if(check("NEWLINE"))
			match("NEWLINE");
		return plague;
	}
	
	private Lexeme ifStatement() 
	{
		Lexeme plague = match("IF");
		match("OPEN_PAREN");
		plague.setLeft(expression());
		match("CLOSE_PAREN");
		plague.setRight(new Lexeme("DO", l.getLex().getLine()));
		plague.getRight().setLeft(block());
		plague.getRight().setRight(optElse());
		return plague;
	}
	
	
	
	private Lexeme anonymousExpression() 
	{
		Lexeme plague = new Lexeme("ANONYMOUS_EXPRESSION", l.getLex().getLine());
		if(check("ANONYMOUS")) 
			plague.setLeft(anonymousDefine());
		else 
			plague.setLeft(anonCall());
		return plague;
	}
	
	private Lexeme varExpr() 
	{
		Lexeme lex = match("VARIABLE");
		Lexeme plague = null;
		if(l.initializerExpressionPending()) 
		{
			plague = lex;
			plague.setLeft(initializerExpression());
		}
		else if(l.objectExpressionPending())
			plague = objectExpression(lex);
		else 
		{
			plague = lex;
			plague.setLeft(optVarAssignment());
		}
		return plague;
	}
	private Lexeme optParameterList() 
	{
		Lexeme plague = null;
		if(check("VARIABLE")) 
		{
			plague = new Lexeme("PARAMETER", l.getLex().getLine());
			plague.setLeft(match("VARIABLE"));
			plague.setRight(optParameterList());
		}
		return plague;
	}
	private Lexeme parameterExpression() 
	{
		Lexeme plague = new Lexeme("PARAMETER_EXPRESSION", l.getLex().getLine());
		match("OPEN_PAREN");
		plague.setLeft(optParameterList());
		match("CLOSE_PAREN");
		return plague;
	}
	private Lexeme whileLoop()
	{
		Lexeme plague = match("WHILE");
		match("OPEN_PAREN");
		plague.setLeft(expression());
		match("CLOSE_PAREN");
		plague.setRight(block());
		return plague;
	}
	private Lexeme optElse() 
	{
		Lexeme plague = null;
		if(check("ELSE")) 
		{
			plague = match("ELSE");
			if(l.ifPending()) 
			{
				plague.setLeft(ifStatement());
			}
			else plague.setLeft(block());
		}
		return plague;
	}
	private Lexeme anonymousDefine() 
	{
		Lexeme plague = match("ANONYMOUS");
		plague.setLeft(parameterExpression());
		plague.setRight(block());
		return plague;
	}
	private Lexeme anonCall() 
	{
		match("OPEN_PAREN");
		Lexeme plague = match("ANONYMOUS");
		plague.setLeft(parameterExpression());
		plague.setRight(new Lexeme("CALL", l.getLex().getLine()));
		plague.getRight().setLeft(block());
		plague.getRight().setRight(initializerExpression());
		match("CLOSE_PAREN");
		return plague;
	}
	private Lexeme objectExpression(Lexeme variable) 
	{
		Lexeme plague = new Lexeme("OBJECT_EXPRESSION", l.getLex().getLine());
		plague.setLeft(variable);
		Lexeme lex = plague.getLeft();
		while(check("DOT")) 
		{
			match("DOT");
			lex.setLeft(match("VARIABLE"));
			lex = lex.getLeft();
		}
		if(check("ASSIGN"))
			plague.setRight(objAssignment());
		else if(l.initializerExpressionPending())
			plague.setRight(initializerExpression());
		return plague;
	}
	private Lexeme optVarAssignment() 
	{
		if(check("ASSIGN")) 
		{
			Lexeme plague = match("ASSIGN");
			plague.setLeft(unary());
			return plague;
		}
		return null;
	}
	
	private Lexeme objAssignment() 
	{
		Lexeme plague = match("ASSIGN");
		plague.setLeft(unary());
		return plague;
	}
	private boolean check(String type) 
	{
		return l.getLex().check(type);
	}
	private Lexeme match(String type) 
	{
		if(!check(type)) 
			Fatal.FATAL("Expected " + type, l.getLex().getLine());
		Lexeme lex = l.getLex();
		l.advance();
		return lex;
	}
	public static void main(String[] args) 
	{
	    if(!args[0].endsWith(".max")) 
	    {
            Fatal.FATAL("Incorrect file type", -1);
        }
        Parser p = new Parser(args[0]);
        PrettyPrinter d = new PrettyPrinter();
		Lexeme plague = p.run();
		d.displayParseTree(plague);
	}
}
