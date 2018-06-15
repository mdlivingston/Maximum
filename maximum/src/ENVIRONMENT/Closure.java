package ENVIRONMENT;

import LEXER.Lexeme;
import PRINTER.PrettyPrinter;
import java.util.ArrayList;



public class Closure {
	
	private Lexeme Tree;
	private ArrayList<Lexeme> parameters;
	private Environment Envy;

	public Closure(Environment def, Lexeme paramTree, Lexeme body) 
	{
		ArrayList<Lexeme> para = new ArrayList<Lexeme>();
		Lexeme current = paramTree;
		while(current != null) {
			para.add(current.getLeft());
			current = current.getRight();
		}
		Envy = def;
		parameters = para;
		Tree = body;
	}
	
	public Closure(Environment def, ArrayList<Lexeme> para, Lexeme body) 
	{
		Envy = def;
		parameters = para;
		Tree = body;
	}
	
	public Environment getDefiningEnvironment() 
	{
		return Envy;
	}

	public ArrayList<Lexeme> getParameters() 
	{
		return parameters;
	}

	public Lexeme getBodyTree() 
	{
		return Tree;
	}
	
	public void displayClosure() 
	{
		System.out.print("Parameters: ");
		for(int i = 0; i < parameters.size(); i++) {
			System.out.print(parameters.get(i).getValue() + " ");
		}
		System.out.print("Body: ");
		PrettyPrinter d = new PrettyPrinter();
		d.displayParseTree(Tree);
		System.out.println("(end body)");
	}
}
