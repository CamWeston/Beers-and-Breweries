package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
 
/**
 *
 * @author Cam Weston
 * 
 * In this class we will be connecting to our SQLite database and executing our queries
 */
public class SQLiteDatabase {
	//If debug == true than output all error messages and control messages
	private final boolean debug = true;
	//Path to beers and breweries database
	private final String dbFilePath = "jdbc:sqlite:src/warmup.db";
	//Hashmap of all commands for user. Used in help method
	public Map<String,String> commands = new HashMap<String,String> ();

	//Constructor
	public SQLiteDatabase(){
		constructCommands();
	}
	
	//Create hashmap of commands for help 
	private void constructCommands(){
		commands.put("exit", "Exit the program");
		commands.put("beers", "Return a list of all beers");
		commands.put("breweries", "Return a list of all breweries");
	}
	
	//Connection class to connect to our db
	//If not working than check if your dbFilePath string above is correct
    private Connection connect() {
        Connection conn = null;
        try {
      
            // create a connection to the database
            conn = DriverManager.getConnection(dbFilePath);
            if(debug) System.out.println("Connection to beers and breweries database established.");
            
        } catch (SQLException e) {
        	if(debug) System.out.println("Could not establish connection.");
            if(debug) System.out.println(e.getMessage());
        } 
        return conn;
    }
    
    //Close connection class 
    private boolean closeConnection(Connection conn){
    	try{
    		conn.close();
    		return true;
    	}
    	catch(SQLException e){
    		System.out.println(e);
    		return false;
    	}
    }
    
    //Output all commands available to user
    public void help(){
    	for(Map.Entry<String,String> command : commands.entrySet()){
    		System.out.println(command.getKey() + " : " + command.getValue());
    	}
    }
    
    //Output all beers and their respective breweries
    public void allBeers(){
    	 String sql = "SELECT Beer.name, Beer.abv, Brewery.name FROM Beer "
    	 			+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id "
    	 			+ "ORDER BY Brewery.name";

         String beerName, breweryName;
         float beerABV;
         
        
         try (Connection conn = this.connect();
              Statement query  = conn.createStatement();
              ResultSet results    = query.executeQuery(sql)){
             
             // loop through the result set and output in print function
        	 // TODO: Find a cleaner way to printout our data
             while (results.next()) {
            	beerName = results.getString(1); 
            	beerABV = results.getFloat(2); 
            	breweryName = results.getString(3); 
            	
                System.out.println(beerName + " " + beerABV + " " + breweryName);
             }
             closeConnection(conn);
         }
         catch (SQLException e) {
        	 if(debug) System.out.println("Error running all beers query.");
        	 if(debug) System.out.println(e.getMessage());    
         }
        
    }
}