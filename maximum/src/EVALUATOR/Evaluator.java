package evaluator;

import java.util.ArrayList;

import ENVIRONMENT.Closure;
import ENVIRONMENT.Environment;
import INPUTOUTPUT.Fatal;
import LEXER.Lexeme;
import PARSER.Parser;
import BUILTINS.*;

public class Evaluator 
{
	
	private Environment thisEnv;
	private Lexeme parseTree;
	private Parser p;
	
	public Evaluator(String fileName) 
	{
		thisEnv = new Environment(null);
		thisEnv.addBuiltins();
		p = new Parser(fileName);
		parseTree = p.run();
	}
	
	public void run() 
	{
		evalStatements(parseTree, thisEnv);
	}
	
	private Lexeme evalStatements(Lexeme tree, Environment env) 
	{
		Lexeme current = tree;
		Lexeme returnVal = null;
		while(current != null) 
		{
			returnVal = evalStatement(p.getStatement(current), env);
			current = current.getRight();
		}
		return returnVal;
	}
	
	private Lexeme evalStatement(Lexeme tree, Environment env) 
	{
		switch(tree.getType()) 
		{
		case "EXPRESSION": return evalExpression(tree.getLeft(), env);
		case "VARIABLE_DEFINE": return evalVariableDefine(tree, env);
		case "FUNCTION": return evalFunctionDefine(tree, env);
		case "ARRAY": return evalArrayDefine(tree, env);
		case "CLASS": return evalClassDefine(tree, env);
		case "OBJECT": return evalObjectDefine(tree, env);
		case "CONDITIONAL": return evalConditional(tree, env);
		case "INCLUDE":	return evalInclude(tree, env);
		default: Fatal.FATAL("Unrecognized expression: " + 
				tree.getType().toLowerCase(), tree.getLine());
		}
		return env.nullLexeme();
	}
	
	private Lexeme evalExpression(Lexeme tree, Environment env) 
	{
		Lexeme unary = p.getUnary(tree);
		switch(unary.getType()) 
		{
		case "INTEGER": return unary;
		case "REAL": return unary;
		case "STRING": return unary;
		case "ANONYMOUS_EXPRESSION": return evalAnonymousExpression(unary, env);
		case "OBJECT_EXPRESSION": return evalObjectExpression(unary, env);
		case "VARIABLE":	return evalVariableExpression(unary, env);
		}
		return env.nullLexeme();
	}
	
	private Lexeme evalVariableDefine(Lexeme tree, Environment env) 
	{
		Lexeme var = p.getVariable(tree);
		Lexeme initializer = p.getInitilizer(tree);
		return env.insertVariable(var, evalVariableInitializerExpression(initializer, env));
	}
	
	private Lexeme evalFunctionDefine(Lexeme tree, Environment env) 
	{
		Lexeme var = p.getVariable(tree);
		Lexeme params = p.getParams(tree);
		Lexeme body = p.getBlock(tree);
		return env.insertFunctionDefinition(var, params, body);
	}
	
	private Lexeme evalArrayDefine(Lexeme tree, Environment env) 
	{
		return env.insertArray(p.getVariable(tree), 
				evalInitializerExpression(p.getArrayInitilizers(tree), env));
	}
	
	private Lexeme evalClassDefine(Lexeme tree, Environment env) 
	{
		Environment classEnv = new Environment(env);
		evalStatements(tree.getRight().getLeft(), classEnv);
		return env.insertClass(p.getVariable(tree), classEnv);
	}
	
	private Lexeme evalObjectDefine(Lexeme tree, Environment env) 
	{
		if(tree.getRight().getLeft() == null)
			return env.insertObject(p.getVariable(tree), null);
		else {
			if(tree.getRight().getLeft().getLeft().getLeft().check("OBJECT_EXPRESSION")) {
				Environment setEnv = (Environment)evalExpression(tree.getRight().getLeft().getLeft(), env).getValue();
				setEnv.setParent(env);
				return env.insertObject(p.getVariable(tree), 
						(Environment)evalExpression(tree.getRight().getLeft().getLeft(), env).getValue());
			}
			else {
				Lexeme className = env.lookupEnv(p.getClassName(tree));
				if(!className.check("CLASS"))
					Fatal.FATAL("Cannot instantiate " + p.getVariable(tree).getValue(),  tree.getLine());;
				Environment classEnv = (Environment) className.getValue();
				Environment objectEnv = classEnv.copy();
				objectEnv.setParent(env);
				if(objectEnv.hasCreate()) 
					evalCreate(tree, p.getObjectDefineInitializers(tree), env, objectEnv);
				return env.insertObject(p.getVariable(tree), objectEnv);
			}
		}
	}
	
	private Lexeme evalConditional(Lexeme tree, Environment env) 
	{
		Lexeme lex = p.getConditional(tree);
		if(lex.check("IF"))
			return evalIfStatement(lex, env);
		else
			return evalWhileLoop(lex, env);
	}
	
	private Lexeme evalInclude(Lexeme tree, Environment env) 
	{
		Environment includeEnv = new Environment(null);
		includeEnv.addBuiltins();
		env.setParent(includeEnv);
		Parser parser = new Parser((String) p.getInclude(tree).getValue());
		Lexeme includeTree = parser.run();
		return evalStatements(includeTree, includeEnv);
	}
	
	private Lexeme evalAnonymousExpression(Lexeme tree, Environment env) 
	{
		if(p.anonymousHasCall(tree)) 
			return evalAnonymousCall(p.getVariable(tree), env);
		else
			return evalAnonymousDefine(p.getVariable(tree), env);
	}
	
	private Lexeme evalObjectExpression(Lexeme tree, Environment env) 
	{
		Lexeme lex = p.getObject(tree);
		Lexeme object = env.lookupEnv(lex);
		Lexeme oldLex = null;
		if(object.getValue() == null) 
		{
			Fatal.FATAL("Trying to access an uninitialized object", tree.getLine());
		}
		Environment objectEnv = (Environment) object.getValue();
		oldLex = lex;
		lex = p.getObject(lex);
		Lexeme var = objectEnv.lookupEnv(lex);
		oldLex = lex;
		lex = p.getObject(lex);
		while(lex != null) {
            if(var.getValue() == null) 
            {
                Fatal.FATAL("Trying to access an uninitialized object", tree.getLine());
            }
            objectEnv = (Environment)var.getValue();
			var = objectEnv.lookupEnv(lex);
			oldLex = lex;
			lex = p.getObject(lex);
		}
		if(tree.getRight() == null) 
		{
			if(var.getValue() == null)
				return env.nullLexeme();
			else
				return var;
		}
		else {
			if(tree.getRight().check("ASSIGN"))
				return objectEnv.set(oldLex, evalExpression(p.getObjectAssignInitilizers(tree), env));
			else if(tree.getRight().check("INITIALIZER_EXPRESSION"))
				return evalFunctionCall(var, evalInitializerExpression(p.getObjectExpressionInitializers(tree), env), 
						objectEnv);
		}
		return env.nullLexeme();
	}
	
	private Lexeme evalVariableExpression(Lexeme tree, Environment env) 
	{
		Lexeme lex = p.getVariableExpression(tree);
		if(lex == null) 
		{
			Lexeme l = env.lookupEnv(tree);
			if(l.getValue() == null)
				return env.nullLexeme();
			else
				return l;
		}
		else if(lex.check("INITIALIZER_EXPRESSION"))
			return evalFunction(tree, env);
		else
			return evalVariableAssign(tree, env);
	}
	
	private Lexeme evalVariableInitializerExpression(Lexeme lex, Environment env) 
	{
		if(lex == null)
			return env.nullLexeme();
		else 
			return evalExpression(lex, env);
	}
	
	private ArrayList<Lexeme> evalInitializerExpression(ArrayList<Lexeme> lex, Environment env) 
	{
		ArrayList<Lexeme> evaluated = new ArrayList<Lexeme>();
		for(int i = 0; i < lex.size(); i++) {
			evaluated.add(evalExpression(lex.get(i), env));
		}
		return evaluated;
	}
	
	private Lexeme evalCreate(Lexeme lex, ArrayList<Lexeme> initializers, Environment parentEnv, Environment env) 
	{
		return evalFunctionCall(env.lookupEnv(new Lexeme("VARIABLE", "create", -1)), 
				evalInitializerExpression(initializers, parentEnv), env);	
	}
	
	private Lexeme evalIfStatement(Lexeme tree, Environment env) 
	{
		Lexeme lex = evalExpression(p.getIfExpression(tree), env);
		if((lex.check("BOOLEAN") && lex.getValue().equals(true)) || 
				(lex.check("INTEGER") && (int)lex.getValue() > 0))
			return evalStatements(p.getIfBlock(tree), env);
		else if(p.hasElse(tree)) {
			if(p.hasElseIf(tree))
				return evalIfStatement(p.getElseIfExpression(tree), env);
			else
				return evalStatements(p.getElseExpression(tree), env);
		}
		return env.nullLexeme();
	}
	
	private Lexeme evalWhileLoop(Lexeme tree, Environment env) 
	{
		return evalWhile(tree, env, null);
	}
	
	private Lexeme evalWhile(Lexeme tree, Environment env, Lexeme returnVal) 
	{
		Lexeme lex = evalExpression(p.getWhileExprssion(tree), env);
		if((lex.check("BOOLEAN") && lex.getValue().equals(false)) ||
			(lex.check("INTEGER") && (int)lex.getValue() <= 0))
			return returnVal;
		else
			return evalWhile(tree, env, evalStatements(p.getWhileBlock(tree), env));
	}
	
	private Lexeme evalAnonymousCall(Lexeme tree, Environment env) 
	{
		Closure c = new Closure(env, p.getAnonymousParameters(tree), p.getAnonymousBlock(tree));
		Environment anonymousEnv = new Environment(c.getDefiningEnvironment());
		ArrayList<Lexeme> values = evalInitializerExpression(p.getAnonymousInitilizers(tree), env);
		ArrayList<Lexeme> parameters = c.getParameters();
		if(parameters.size() != values.size()) {
			Fatal.FATAL("Invalid number of arguments to function " + tree.getValue(), tree.getLine());
		}
		for(int i = 0; i < parameters.size(); i++) {
			anonymousEnv.insertVariable(parameters.get(i), values.get(i));
		}
		return evalStatements(c.getBodyTree(), anonymousEnv);
	}
	
	private Lexeme evalAnonymousDefine(Lexeme tree, Environment env) 
	{
		return new Lexeme("FUNCTION", new Closure(env, tree.getLeft().getLeft(), 
				tree.getRight().getLeft()), tree.getLine());
	}
	
	private Lexeme evalFunction(Lexeme tree, Environment env) 
	{
		if(Builtin.builtin(tree))
			return Builtin.evalBuiltin(tree, evalInitializerExpression(p.getFunctionInitializers(tree), env), env);
		else if(andBuiltin(tree))
			return evalAnd(tree, env);
		else if(orBuiltin(tree))
			return evalOr(tree, env);
		else
			return evalNonBuiltin(tree, env);
	}
	
	private Lexeme evalVariableAssign(Lexeme tree, Environment env) 
	{
		Lexeme lex = tree.getLeft().getLeft();
		Lexeme l =  evalExpression(lex, env);
		if( l.check("CLASS")) {
			Environment classEnv = (Environment) env.lookupEnv(lex.getLeft()).getValue();
			Environment objectEnv = classEnv.copy();
			objectEnv.setParent(env);
			if(objectEnv.hasCreate())
				evalCreate(lex, p.getFunctionInitializers(tree.getLeft().getLeft().getLeft()), env, objectEnv);
			return env.set(tree, new Lexeme("OBJECT", objectEnv, tree.getLine()));
		}
			return env.set(tree, l);
	}
	
	private Lexeme evalFunctionCall(Lexeme function, ArrayList<Lexeme> initializers, Environment env) 
	{
		if(function.check("CLASS"))
			return function;
		else {
			if(!function.check("FUNCTION")) {
				Fatal.FATAL("cannot call " + function.getValue() +" -- not a function", function.getLine());
			}
			Closure c = (Closure)function.getValue();
			Environment functionEnv = new Environment(c.getDefiningEnvironment());
			ArrayList<Lexeme> parameters = c.getParameters();
			if(parameters.size() != initializers.size()) {
				Fatal.FATAL("Invalid number of arguments to function " 
			+ function.getValue().toString(), function.getLine());
			}
			for(int i = 0; i < parameters.size(); i++) {
				functionEnv.insertVariable(parameters.get(i), initializers.get(i));
			}
			return evalStatements(c.getBodyTree(), functionEnv);
		}
	}
	
	private boolean andBuiltin(Lexeme tree) 
	{
		return tree.getValue().equals("and");
	}
	
	private Lexeme evalAnd(Lexeme tree, Environment env) 
	{
		Lexeme a = evalExpression(p.getFirstExpression(tree), env);
		if(a.getValue().equals(true)) 
		{
			Lexeme b = evalExpression(p.getSecondExpression(tree), env);
			if(b.getValue().equals(true))
				return new Lexeme("BOOLEAN", true, -1);
			else
				return new Lexeme("BOOLEAN",false, -1);
		}
		else
			return new Lexeme("BOOLEAN", false, -1);				
	}
	
	private boolean orBuiltin(Lexeme tree) 
	{
		return tree.getValue().equals("or");
	}
	
	private Lexeme evalOr(Lexeme tree, Environment env) 
	{
		Lexeme a = evalExpression(p.getFirstExpression(tree), env);
		if(a.getValue().equals(true)) 
			return new Lexeme("BOOLEAN", true, -1);
		else {
			Lexeme b = evalExpression(p.getSecondExpression(tree), env);
			if(b.getValue().equals(true))
				return new Lexeme("BOOLEAN", true, -1);
			else
				return new Lexeme("BOOLEAN", false, -1);		
		}		
	}

	private Lexeme evalNonBuiltin(Lexeme tree, Environment env) 
	{
		return evalFunctionCall(env.lookupEnv(tree), evalInitializerExpression(p.getFunctionInitializers(tree), env), env);
	}
	
	public static void main(String[] args) 
	{
	    if(!args[0].endsWith(".max")) {
            Fatal.FATAL("Incorrect file type", -1);
        }
        Evaluator e = new Evaluator(args[0]);
		e.run();
	}
}
