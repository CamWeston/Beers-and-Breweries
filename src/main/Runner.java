package main;

import database.SQLiteDatabase;
import userInput.InputRunner;

/** 
 * @author CamWeston
 *
 *	Client Deployment Class
 * 
 */


public class Runner {

	public static void main(String[] args) {
		SQLiteDatabase db = new SQLiteDatabase();
		
		System.out.println("Starting user input runner");
		InputRunner ir = new InputRunner(db);
	}

}
