package userInput;

import java.util.Scanner;

import database.SQLiteDatabase;
//In this class we will control user interactions 

public class InputRunner {
	//Database obj passed in during constructor
	private SQLiteDatabase db;
	//Scanner for user input of commands
	private final Scanner userInput = new Scanner(System.in);
	
	//Constructor
	public InputRunner(SQLiteDatabase database){
		db = database;
		run();
	}	
	
	//Execute main loop
	private void run(){
		String currentCommand = "";
		while(!currentCommand.toLowerCase().equals("exit")){
			System.out.print("Enter a command (type 'help' for a list of commands): ");
			currentCommand = userInput.nextLine();
			userController(currentCommand);
		}
	}
	
	//Control board to decide which db func to run
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
