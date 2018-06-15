package INPUTOUTPUT;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.EOFException;
import java.io.PushbackReader;
import java.io.FileInputStream;
import java.util.Scanner;
import java.io.Reader;

public class FileInput 
{
	private FileInputStream file;
	private Scanner scanner;
	private PushbackReader buffer;
	private Reader reader;
	private boolean endOfFile;
	
	public FileInput(String fileName) 
	{
		try 
		{
			this.file = new FileInputStream(fileName);
		}
		catch(IOException e) 
		{
			System.err.println(e.getMessage());
			System.exit(1);
		}
		createScanner();
		createBuffer();
	}
	
	public boolean endOfFile() 
	{
		return scanner.next() == null;
	}
	
	public String readNextString() 
	{
		return scanner.next();
	}
	
	public Character readNextCharacter() throws EOFException 
	{
		int val = 0;
		skipWhitespace();
		try 
		{
			val = buffer.read();
		} 
		catch (IOException e) 
		{
				e.printStackTrace();
		}
		if(val == -1) 
		{
				endOfFile = true;
		}
		return new Character((char) val);
	}
	
	public Character readNextRawCharacter() throws EOFException 
	{
		int val = 0;
		try 
		{
			val = buffer.read();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		if(val == -1) 
		{
			throw new EOFException();
		}
		return new Character((char) val);
	}
	
	public void pushbackCharacter(char c)
	{
		try 
		{
			buffer.unread((int) c);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void close() 
	{
		scanner.close();
		try 
		{
			reader.close();
		} 
		catch (IOException e1) 
		{
			System.err.println(e1.getMessage());
		}
		try 
		{
			buffer.close();
		} 
		catch (IOException e2) 
		{
			System.err.println(e2.getMessage());
		}
		try 
		{
			file.close();
		} 
		catch (IOException e3) 
		{
			System.err.println(e3.getMessage());
		}
	}
	public void skipWhitespace() throws EOFException 
	{
		Character c = readNextRawCharacter();
		while(isWhitespace(c) && !endOfFile) 
		{
			c = readNextRawCharacter();
		}
		pushbackCharacter(c);
	}

	private boolean isWhitespace(Character c) 
	{
		return c == ' ' || c == '\t' || c == '\r';
	}
	
	private void createScanner() 
	{
		this.scanner = new Scanner(file);
	}
	
	private void createBuffer() 
	{
		this.reader = new InputStreamReader(file);
		this.buffer = new PushbackReader(reader, 10);
	}
}
