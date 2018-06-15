package INPUTOUTPUT;

public class Fatal 
{
	public static void FATAL(String message, Integer line) 
	{
		System.out.println("Error on line " + line + ":");
		System.out.println("\t" + message);
		System.exit(1);
	}

}
