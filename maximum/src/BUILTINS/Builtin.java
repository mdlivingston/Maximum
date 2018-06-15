package BUILTINS;

import java.util.ArrayList;

import ENVIRONMENT.Environment;
import INPUTOUTPUT.Fatal;
import LEXER.Lexeme;

public class Builtin {

	public static boolean builtin(Lexeme plague) 
	{
		return
			plague.getValue().equals("addition") || plague.getValue().equals("subtraction") ||
			plague.getValue().equals("multiplication") || plague.getValue().equals("division") ||
			plague.getValue().equals("raise") ||
			plague.getValue().equals("equals") ||
			plague.getValue().equals("notequals") ||
			plague.getValue().equals("greater") || plague.getValue().equals("lesser") || plague.getValue().equals("greaterEqual") || plague.getValue().equals("lessEqual") ||
			plague.getValue().equals("increment") || plague.getValue().equals("decrement") ||
			plague.getValue().equals("print") || plague.getValue().equals("println") ||
			plague.getValue().equals("displayArray") || plague.getValue().equals("appendArray") || plague.getValue().equals("prependArray") ||
			plague.getValue().equals("getArray") || plague.getValue().equals("setArray") ||
			plague.getValue().equals("removeArray") || plague.getValue().equals("sizeArray");
	}
			
	public static Lexeme evalBuiltin(Lexeme builtin, ArrayList<Lexeme> operands, Environment env) 
	{
		String maximus = (String) builtin.getValue();
		int theLine = builtin.getLine();
		if(maximus.equals("addition"))
			return addBuiltin(operands, env, theLine);
		else if(maximus.equals("subtraction"))
			return subBuiltin(operands, env, theLine);
		else if(maximus.equals("multiplication"))
			return multBuiltin(operands, env, theLine);
		else if(maximus.equals("division"))
			return divBuiltin(operands, env, theLine);
		else if(maximus.equals("raise"))
			return raiseBuiltin(operands, env, theLine);
		else if(maximus.equals("equals"))
			return eqBuiltin(operands, env, theLine);
		else if(maximus.equals("notequals"))
			return neqBuiltin(operands, env, theLine);
		else if(maximus.equals("greater"))
			return greaterBuiltin(operands, env, theLine);
		else if(maximus.equals("lesser"))
			return lessBuiltin(operands, env, theLine);
		else if(maximus.equals("greaterEqual"))
			return greaterEqBuiltin(operands, env, theLine);
		else if(maximus.equals("lessEqual"))
			return lessEqBuiltin(operands, env, theLine);
		else if(maximus.equals("increment"))
			return incBuiltin(operands, env, theLine);
		else if(maximus.equals("decrement"))
			return decBuiltin(operands, env, theLine);
		else if(maximus.equals("print"))
			return printBuiltin(operands, env, theLine);
		else if(maximus.equals("println"))
			return printlnBuiltin(operands, env, theLine);
		else if(maximus.equals("displayArray"))
			return displayArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("appendArray"))
			return appendArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("prependArray"))
			return prependArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("getArray"))
			return getArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("setArray"))
			return setArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("removeArray"))
			return removeArrayBuiltin(operands, env, theLine);
		else if(maximus.equals("sizeArray"))
			return sizeArrayBuiltin(operands, env, theLine);
		else
			Fatal.FATAL("Function " + builtin.getValue() + " is not builtin", theLine);
		return null;
	}
	

	private static Lexeme addBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		 if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin addition", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("INTEGER", op + (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op + Double.parseDouble(op2.getValue().toString()), line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("REAL", op + (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op + Double.parseDouble(op2.getValue().toString()), line);
		}
		else if(op1.getType().equals("STRING")) {
			String op = op1.getValue().toString();
			if(op2.getType().equals("STRING")) 
				return new Lexeme("STRING", op + op2.getValue().toString(), line);
		}
		Fatal.FATAL("Cannot add with the given operands", line);
		return env.nullLexeme();
	}
	
	private static Lexeme subBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin subtraction", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("INTEGER", op - (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op - Double.parseDouble(op2.getValue().toString()), line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("REAL", op - (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op - Double.parseDouble(op2.getValue().toString()), line);
		}
		Fatal.FATAL("Cannot subtract with the given operands", line);
		return env.nullLexeme();
	}

	private static Lexeme multBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin multiplication", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("INTEGER", op * (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op * Double.parseDouble(op2.getValue().toString()), line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("REAL", op * (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op * Double.parseDouble(op2.getValue().toString()), line);
		}
		Fatal.FATAL("Cannot multiply with the given operands", line);
		return env.nullLexeme();
		}
	
	private static Lexeme divBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin division", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op2.check("INTEGER") && (int)op2.getValue() == 0)
			Fatal.FATAL("Divide by zero", line);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("INTEGER", op / (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op / Double.parseDouble(op2.getValue().toString()), line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("REAL", op / (int)op2.getValue(), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", op / Double.parseDouble(op2.getValue().toString()), line);
		}
		Fatal.FATAL("Cannot divide with the given operands", line);
		return env.nullLexeme();
		}
	
	private static Lexeme raiseBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin raise", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("INTEGER", (int)Math.pow(op, (int)op2.getValue()), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", Math.pow(op, Double.parseDouble(op2.getValue().toString())), line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER"))
				return new Lexeme("REAL", Math.pow(op, (int)op2.getValue()), line);
			else if(op2.getType().equals("REAL"))
				return new Lexeme("REAL", Math.pow(op, Double.parseDouble(op2.getValue().toString())), line);
		}
		Fatal.FATAL("Cannot divide with the given operands", line);
		return env.nullLexeme();
		}
	
	private static Lexeme eqBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin eq", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				if(op == (int)op2.getValue())
					return new Lexeme("BOOLEAN", true, line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("REAL"))
				if(op == Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
		}
		else if(op1.getType().equals("STRING")) 
		{
			if(op2.getType().equals("STRING"))
				if(op1.getValue().toString().equals(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
		}
		else if(op1.getType().equals("NULL")) 
		{
			if(op2.getType().equals("NULL"))
				return new Lexeme("BOOLEAN", true, line);
		}
		else if(op1.getType().equals("OBJECT")) 
		{
			if(op2.getType().equals("OBJECT"))
				if(op1.getValue().equals(op2.getValue()))
					return new Lexeme("BOOLEAN", true, line);
		}
		return new Lexeme("BOOLEAN", false, line);
 	}
	
	private static Lexeme neqBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin neq", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER"))
				if(op == (int)op2.getValue())
					return new Lexeme("BOOLEAN", false, line);
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("REAL"))
				if(op == Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", false, line);
		}
		else if(op1.getType().equals("STRING")) 
		{
			if(op2.getType().equals("STRING"))
				if(op1.getValue().toString().equals(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", false, line);
		}
		else if(op1.getType().equals("NULL")) 
		{
			if(op2.getType().equals("NULL"))
				return new Lexeme("BOOLEAN", false, line);
		}
		else if(op1.getType().equals("OBJECT")) 
		{
			if(op2.getType().equals("OBJECT"))
				if(op1.getValue().equals(op2.getValue()))
					return new Lexeme("BOOLEAN", false, line);
		}
		return new Lexeme("BOOLEAN", true, line);
 	}
	
	private static Lexeme greaterBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin greater", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) {
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER")) 
			{
				if(op > (int)op2.getValue())
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op > Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER")) 
			{
				if(op > (int)op2.getValue())
				return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL"))
			 {
				if(op > Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("STRING")) 
		{
			String op = op1.getValue().toString();
			if(op2.getType().equals("STRING")) {
				if(op.compareTo(op2.getValue().toString()) > 0)
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		Fatal.FATAL("Cannot compare the given operands", line);
		return env.nullLexeme();
	}
	
	private static Lexeme lessBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2){
			Fatal.FATAL("Invalid # of args to builtin lesser", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER")) 
			{
				if(op < (int)op2.getValue())
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op < Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER")) 
			{
				if(op < (int)op2.getValue())
				return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op < Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("STRING")) 
		{
			String op = op1.getValue().toString();
			if(op2.getType().equals("STRING")) 
			{
				if(op.compareTo(op2.getValue().toString()) < 0)
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		Fatal.FATAL("Cannot compare the given operands", line);
		return env.nullLexeme();
	}
	
	private static Lexeme greaterEqBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin greaterEqual", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER")) 
			{
				if(op >= (int)op2.getValue())
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op >= Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER")) 
			{
				if(op >= (int)op2.getValue())
				return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op >= Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("STRING")) 
		{
			String op = op1.getValue().toString();
			if(op2.getType().equals("STRING")) 
			{
				if(op.compareTo(op2.getValue().toString()) >= 0)
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		Fatal.FATAL("Cannot compare the given operands", line);
		return env.nullLexeme();
	}
	
	private static Lexeme lessEqBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin lessEqual", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType().equals("INTEGER")) 
		{
			int op = (int)op1.getValue();
			if(op2.getType().equals("INTEGER")) 
			{
				if(op <= (int)op2.getValue())
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.getType().equals("REAL")) 
			{
				if(op <= Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.getType().equals("REAL")) 
		{
			Double op = Double.parseDouble(op1.getValue().toString());
			if(op2.getType().equals("INTEGER")) 
			{
				if(op <= (int)op2.getValue())
				return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
			else if(op2.check("REAL")) 
			{
				if(op <= Double.parseDouble(op2.getValue().toString()))
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		else if(op1.check("STRING")) 
		{
			String op = op1.getValue().toString();
			if(op2.check("STRING")) 
			{
				if(op.compareTo(op2.getValue().toString()) <= 0)
					return new Lexeme("BOOLEAN", true, line);
				else
					return new Lexeme("BOOLEAN", false, line);
			}
		}
		Fatal.FATAL("Cannot compare the given operands", line);
		return env.nullLexeme();
	}
	
	private static Lexeme incBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 1)
		{
			Fatal.FATAL("Invalid # of args to builtin increment", line);
		}
		Lexeme op1 = operands.get(0);
		if(op1.check("INTEGER")) 
			return new Lexeme(op1.getType(), (int)op1.getValue() + 1, line);
		Fatal.FATAL("Cannot increment the given operand", line);
		return env.nullLexeme();
	}
	
	private static Lexeme decBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 1)
		{
			Fatal.FATAL("Invalid # of args to builtin decrement", line);
		}
		Lexeme op1 = operands.get(0);
		if(op1.check("INTEGER")) 
			return new Lexeme(op1.getType(), (int)op1.getValue() - 1, line);
		Fatal.FATAL("Cannot increment the given operand", line);
		return env.nullLexeme();
	}
	
	private static Lexeme printBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		for(int i = 0; i < operands.size(); i++) {
			Lexeme theLine = operands.get(i);
			if(theLine.check("NULL"))
				System.out.print("null ");
			else
				System.out.print(theLine.getValue());
		}
		return env.nullLexeme();
	}
	
	private static Lexeme printlnBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		for(int i = 0; i < operands.size(); i++) 
		{
			Lexeme theLine = operands.get(i);
			if(theLine.check("NULL"))
				System.out.print("null ");
			else
				System.out.print(theLine.getValue());
		}
		System.out.println();
		return env.nullLexeme();
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme displayArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 1)
		{
			Fatal.FATAL("Invalid # of args to builtin display", line);
		}
		Lexeme op1 = operands.get(0);

		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Attempting to use display on something not of type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		for(int i = 0; i < vals.size(); i++) {
			System.out.print(vals.get(i).getValue() + " ");
		}
		System.out.println();
		return env.nullLexeme();
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme appendArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin append", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Cannot use something that is not type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		vals.add(op2);
		return new Lexeme("ARRAY", vals, line);
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme prependArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin prepend", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Cannot use something that is not type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		vals.add(0, op2);
		return new Lexeme("ARRAY", vals, line);
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme getArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin get", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Attempting to use get on something not of type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		if(!op2.check("INTEGER"))
			Fatal.FATAL("Argument 2 of get not of type integer", line);
		return vals.get((int)op2.getValue());
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme setArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 3)
		{
			Fatal.FATAL("Invalid # of args to builtin prepend", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		Lexeme op3 = operands.get(2);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Cannot use something that is not type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		if(!op2.check("INTEGER"))
			Fatal.FATAL("Argument 2 of get not of type integer", line);
		vals.set((int)op2.getValue(), op3);
		return new Lexeme("ARRAY", vals, line);
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme removeArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 2)
		{
			Fatal.FATAL("Invalid # of args to builtin prepend", line);
		}
		Lexeme op1 = operands.get(0);
		Lexeme op2 = operands.get(1);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Cannot use something that is not type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		if(!op2.check("INTEGER"))
			Fatal.FATAL("Argument 2 of get not of type integer", line);
		vals.remove((int)op2.getValue());
		return new Lexeme("ARRAY", vals, line);
	}
	
	@SuppressWarnings("unchecked")
	private static Lexeme sizeArrayBuiltin(ArrayList<Lexeme> operands,  Environment env, int line) 
	{
		if(operands.size() != 1)
		{
			Fatal.FATAL("Invalid # of args to builtin prepend", line);
		}
		Lexeme op1 = operands.get(0);
		
		if(op1.getType() != "ARRAY") 
			Fatal.FATAL("Cannot use something that is not type array", line);
		ArrayList<Lexeme> vals = (ArrayList<Lexeme>) op1.getValue();
		return new Lexeme("INTEGER", vals.size(), line);
	}
}
