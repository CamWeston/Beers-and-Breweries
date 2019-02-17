package userInput;

import java.util.Scanner;
import java.util.Arrays;

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
	private boolean handleComplexCommand(String command) {
		command = command.trim();
		String commandLC = command.toLowerCase();
		String[] subcommandLC = commandLC.split(" ");
		String[] subcommand = command.split(" ");
		if(subcommand.length<3)
			return false;
		
		if(subcommandLC[0].equals("brewery")||subcommandLC[0].equals("beer")) {
			if(subcommandLC[1].equals("breweryname") || subcommandLC[1].equals("beername")) {
				//'name' will be assigned to attribute, as the user will search
				//'beer brewery name <brewery name>', for example
				String[] attribute = {"name"};
				//The user entered name of the Brewery will be assigned to name
				String[] name = {String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length))};
				db.complexSearch(subcommandLC[1], attribute, name);
				return true;
			}
			else if(subcommandLC[1].equals("name") || subcommandLC[1].equals("state") || subcommandLC[1].equals("description")){
				String[] attribute = {subcommandLC[1].trim()};
				String[] name = {String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length))};
				db.complexSearch(subcommandLC[0],attribute, name);
				return true;
			}
			else if(subcommandLC[1].equals("city")) {

				int state = -1;
				
				for(int i=1; i<subcommandLC.length; i++) {
					if(subcommandLC[i].equals("state")) {
						state = i;
						break;
					}
				}
				if(state!=-1) {
					String[] attribute = {subcommandLC[1].trim(), subcommandLC[state].trim()};
					String[] name = {
							String.join(" ", Arrays.copyOfRange(subcommand, 2, state)),
							String.join(" ", Arrays.copyOfRange(subcommand, state+1, subcommand.length))};
					db.complexSearch(subcommandLC[0], attribute, name);
					return true;
				}
				else {
					String[] attribute = {subcommandLC[1].trim()};
					String[] name = {String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length))};
					db.complexSearch(subcommandLC[0],attribute, name);
					return true;
				}
			}
		}
		else if(subcommandLC[0].equals("abv")) {
			if(subcommandLC[1].equals("beer") || subcommandLC[1].equals("brewery")) {
				db.abvSearch(subcommandLC[1], String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length)));
				return true;
			}
		}
		return false;
	}

	
	//Control board to decide which db func to run
	private void userController(String command){
		String commandLC = command.toLowerCase();
		
		if(commandLC.equals("help")){
			db.help();
		}
		else if(commandLC.equals("allbeers")){
			db.allBeers();	
		}
		else if(commandLC.equals("allbreweries")){
			db.allBreweries();	
		}
		else if(commandLC.equals("exit")){
			System.out.println("Goodbye!");
		}
		else if(handleComplexCommand(command)==false){
			System.out.println("Command not found (typer 'help' for a list of commands)");
		}
	}
}
