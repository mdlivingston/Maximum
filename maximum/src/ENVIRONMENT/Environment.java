package ENVIRONMENT;

import java.util.ArrayList;

import BUILTINS.Builtin;
import INPUTOUTPUT.Fatal;
import LEXER.Lexeme;

public class Environment {
	
	private Environment elder;
	private ArrayList<Lexeme> vars;
	private ArrayList<Lexeme> values; 
	
	public void addBuiltins() 
	{
		vars.add(new Lexeme("VARIABLE", "true", -1));
		values.add(new Lexeme("BOOLEAN", true, -1));
		vars.add(new Lexeme("VARIABLE", "false", -1));
		values.add(new Lexeme("BOOLEAN", false, -1));
		vars.add(new Lexeme("VARIABLE", "null", -1));
		values.add(nullLexeme());
	}

	public void setParent(Environment e) 
	{
		elder = e;
	}
	
	public Environment(Environment p) 
	{
		setParent(p);
		vars = new ArrayList<Lexeme>();
		values = new ArrayList<Lexeme>();
	}
	
	public Lexeme nullLexeme() 
	{
		return new Lexeme("NULL", null, -1);
	}
	public Lexeme insertArray(Lexeme var, ArrayList<Lexeme> vals) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		vars.add(var);	
		Lexeme l = new Lexeme("ARRAY", vals, var.getLine());
		values.add(l);
		return l;
	}
	
	
	public void display() 
	{
		if(getParent() != null) {
			getParent().display();
		}
		this.displayEnv();
	}
		
	public Lexeme insertVariable(Lexeme var, Lexeme val) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		vars.add(var);
		values.add(val);
		return val;
	}
	
	
	public Lexeme insertFunctionDefinition(Lexeme var, Lexeme parameterTree, Lexeme body) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		if(Builtin.builtin(var)) 
			Fatal.FATAL("Cannot redefine builtin", var.getLine());
		vars.add(var);
		Closure c = new Closure(this, parameterTree, body.getLeft());
		Lexeme l = new Lexeme("FUNCTION", c, var.getLine());
		values.add(l);
		return l;
	}
	
	public Lexeme insertFunctionDefinition(Lexeme var, ArrayList<Lexeme> params, Lexeme body) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		if(Builtin.builtin(var)) 
			Fatal.FATAL("Cannot redefine builtin", var.getLine());
		vars.add(var);
		Closure c = new Closure(this, params, body);
		Lexeme l = new Lexeme("FUNCTION", c, var.getLine());
		values.add(l);
		return l;
	}
	
	public Lexeme insertObject(Lexeme var, Environment env) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		vars.add(var);
		Lexeme l = null;
		if(env == null) 
			l = new Lexeme("NULL", null, var.getLine());
		else 
			l = new Lexeme("OBJECT", env, var.getLine());
		values.add(l);
		return l;
	}
	
	public Lexeme insertClass(Lexeme var, Environment env) 
	{
		checkIfKeyword(var);
		checkIfAlreadyDefinedInLocal(var);
		vars.add(var);
		Lexeme l = new Lexeme("CLASS", env, var.getLine());
		values.add(l);
		return l;
	}
	
	public Environment copy() 
	{
		Environment env = new Environment(null);
		for(int i = 0; i < vars.size(); i++) {
			Lexeme var = new Lexeme("VARIABLE", vars.get(i).getValue(), vars.get(i).getLine());
			Lexeme val = values.get(i);
			if(val != null) 
			{
				if(val.check("ARRAY"))
					env.insertArray(var, new ArrayList<Lexeme>());
				else if(val.check("FUNCTION"))
					env.insertFunctionDefinition(var, ((Closure) val.getValue()).getParameters(), ((Closure) val.getValue()).getBodyTree());
				else if(val.check("OBJECT"))
					env.insertObject(var, ((Environment) val.getValue()));
				else
					env.insertVariable(vars.get(i), new Lexeme(val.getType(), val.getValue(), val.getLine()));
			}
			else env.insertVariable(var, null);
		}
		return env;
	}
	
	private void checkIfKeyword(Lexeme var) 
	{
		String val = var.getValue().toString();
		if(val.equals("array") 
			|| val.equals("max") 
			|| val.equals("maximus") 
			|| val.equals("object") 
			|| val.equals("else") 
			|| val.equals("while") 
			|| val.equals("class") 
			|| val.equals("if") 
			|| val.equals("true") 
			|| val.equals("false") 
			|| val.equals("include") 
			|| val.equals("mystery") 
			||val.equals("null") || val.equals("this"))

			Fatal.FATAL("Cannot redefine keyword " + var.getValue(), var.getLine());
	}
	
	public void checkIfAlreadyDefinedInLocal(Lexeme var) 
	{
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getValue().equals(var.getValue()))
				Fatal.FATAL(var.getValue() + " already defined", var.getLine());
		}
	}
	
	public Lexeme set(Lexeme var, Lexeme val) 
	{
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getValue().equals(var.getValue())) {
				values.set(i, val);
				return values.get(i);
			}
		}
		if(getParent() != null)	
			return getParent().set(var, val);
		else 
			Fatal.FATAL(var.getValue() + " not defined", var.getLine());
		return null;
	}
	
	public Lexeme lookupEnv(Lexeme var) 
	{
		if(var.getValue().equals("this")) 
		{
			return new Lexeme("THIS", this, -1);
		}
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getValue().equals(var.getValue())) 
				return values.get(i);
		}
		if(getParent() != null)	
			return getParent().lookupEnv(var);
		else
			Fatal.FATAL(var.getValue() + " not defined", var.getLine());
		return null;
	}	
	
	@SuppressWarnings("unchecked")
	public void displayEnv() 
	{
		for(int i = 0; i < vars.size(); i ++) {
			System.out.print(vars.get(i).getValue() + ": ");
			String key = values.get(i).getType();
			switch(key) {
			case "VARIABLE": {
				Lexeme val = (Lexeme) values.get(i).getValue();
				if(val != null)
					System.out.println(val.getValue());
				else
					System.out.println();
				break;
				}
			case "FUNCTION": {
				Closure closure = (Closure)values.get(i).getValue();
				closure.displayClosure();
				break;
				}
			case "ARRAY": {
				ArrayList<Lexeme> vals = (ArrayList<Lexeme>) values.get(i).getValue();
				for(int j = 0; j < vals.size(); j++) {
					System.out.print(vals.get(j).getValue() + " ");
				}
				System.out.println();
				break;
				}
			case "ENV": {
				System.out.println( "(environment)");
				Environment e = (Environment) values.get(i).getValue();
				e.displayEnv();
				System.out.println("(end environment)");
			}
			}
		}
	}

	public Environment getParent() 
	{
		return elder;
	}

	public boolean hasCreate() 
	{
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getValue().equals("create"))
				return true;
		}
		return false;
	}

	public Closure getCreate() 
	{
		for (int i = 0; i < vars.size(); i++) {
			if(vars.get(i).getValue().equals("create"))
				return (Closure)values.get(i).getValue();
		}
		return null;
	}
}
