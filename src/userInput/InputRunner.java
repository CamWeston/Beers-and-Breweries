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
	
	
	/**
	 * This function will take a complex command (a command with several parameters) (i.e. beer name <userInput)
	 * and process it accordingly, passing type of table the user is reference, along with attributes (corresponding to columns in table),
	 * and name, corresponding to what the user is searching for specifically in the column of that table.
	 * 
	 * @param command
	 * @return true or false, if command was passed to DB class
	 */

	private boolean handleComplexCommand(String command) {
		//First break up the entered command string into an array
		command = command.trim();
		String commandLC = command.toLowerCase();
		String[] subcommandLC = commandLC.split(" ");
		String[] subcommand = command.split(" ");
		
		
		//No command exists that is less than length 3
		if(subcommand.length<3)
			return false;
		
		//First if determines if the user is searching by beer, brewery, or abv
		if(subcommandLC[0].equals("brewery")||subcommandLC[0].equals("beer")) {
			
			//These inputs correspond to queries with JOIN clauses, such as searching a brewery by a name of the beer it brews
			if(subcommandLC[1].equals("breweryname") || subcommandLC[1].equals("beername")) {
				//'name' will be assigned to attribute, as the user will search
				//'beer brewery name <brewery name>', for example
				String[] attribute = {"name"};
				//The user entered name of the Brewery will be assigned to name
				String[] name = {String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length))};
				db.complexSearch(subcommandLC[1], attribute, name);
				return true;
			}
			
			//These inputs do not require a JOIN clause when passed to db
			else if(subcommandLC[1].equals("name") || subcommandLC[1].equals("state") || subcommandLC[1].equals("desc")){
				//Attribute gets assigned by column queried
				String[] attribute = {subcommandLC[1].trim()};
				//User input gets assigned to name
				String[] name = {String.join(" ", Arrays.copyOfRange(subcommand, 2, subcommand.length))};
				db.complexSearch(subcommandLC[0],attribute, name);
				return true;
			}
			//This is for the special case of searching by both city and state
			else if(subcommandLC[1].equals("city")) {

				int state = -1;
				
				for(int i=1; i<subcommandLC.length; i++) {
					if(subcommandLC[i].equals("state")) {
						state = i;
						break;
					}
				}
				if(state!=-1) {
					//attribute is given "city" and "state"
					String[] attribute = {subcommandLC[1].trim(), subcommandLC[state].trim()};
					
					//name is given whatever city and state strings the user enters
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
		
		//This case handles inputs searching by abv
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

