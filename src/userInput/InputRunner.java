package userInput;

import java.util.Scanner;

import database.SQLiteDatabase;
//In this class we will control user interactions 

public class InputRunner {
	private SQLiteDatabase db;
	private final Scanner userInput = new Scanner(System.in);
	
	
	public InputRunner(SQLiteDatabase database){
		db = database;
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
		}
		else if(command.equals("beers")){
			db.allBeers();	
		}
		else if(command.toLowerCase().equals("exit")){
			System.out.println("Goodbye!");
		}
		else{
			System.out.println("Command not found (typer 'help' for a list of commands)");
		}
	}
}
