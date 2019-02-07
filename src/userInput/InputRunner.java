package userInput;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

import database.SQLiteDatabase;

//In this class we will control user interactions 

public class InputRunner {
	private SQLiteDatabase db;
	private final Scanner userInput = new Scanner(System.in);
	
	
	public InputRunner(SQLiteDatabase db){
		db = this.db;
		run();
	}	
	
	private void run(){
		String currentCommand = "";
		while(!currentCommand.toLowerCase().equals("exit")){
			System.out.print("Enter a command (type 'help' for a list of commands): ");
			currentCommand = userInput.nextLine();
			userController(currentCommand);
		}
	}
	
	private void userController(String command){
		command = command.toLowerCase();
		
		if(command.equals("help")){
			db.help();
			return;
		}
		else if(command.toLowerCase().equals("exit")){
			System.out.println("Goodbye!");
			return;
		}
	}
}
