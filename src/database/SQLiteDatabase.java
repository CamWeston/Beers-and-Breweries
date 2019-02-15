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
	private final String dbFilePath = "jdbc:sqlite:src/database/warmup.db";
	//Hashmap of all commands for user. Used in help method
	public Map<String,String> commands = new HashMap<String,String> ();

	//Constructor
	public SQLiteDatabase(){
		constructCommands();
	}
	
	//Create hashmap of commands for help 
	private void constructCommands(){
		commands.put("exit", "\t\t\t\tExit the program");
		commands.put("allbeers", "\t\t\tReturn a list of all beers");
		commands.put("allbreweries", "\t\t\tReturn a list of all breweries");

		commands.put("brewery name (beerName)", "\tReturn brewery by a specific beer");
		commands.put("brewery city (cityName)", "\tReturn a list of all breweries by city name");
		commands.put("brewery state (stateName)", "\tReturn a list of all breweries by state name");
		commands.put("brewery city (cityName)	state (stateName)", "Return a list of all breweries by city and state name");
		
		commands.put("beer name (breweryName)", "\tReturn a list of all beers by brewery name");
		commands.put("beer state (stateName)", "\t\tReturn a list of all beers by state name");
		commands.put("beer city (cityName)", "\t\tReturn a list of all beers by city name");
		commands.put("beer city (cityName) state (stateName)", "Return a list of all beers by city and state name");
		
		commands.put("abv beer (beerName)", "\t\tReturn abv by beer name");
		commands.put("abv brewery (breweryName)", "\tReturn abv by brewery name");

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
  
    //Output all commands available to user
    public void help(){
    	for(Map.Entry<String,String> command : commands.entrySet()){
    		System.out.println(command.getKey() + " : " + command.getValue());
    	}
    }
    public void getResult (String sql, int numObject){
    	
	     boolean find = false;
	     try (Connection conn = this.connect();
	          Statement query  = conn.createStatement();
	          ResultSet results = query.executeQuery(sql)){
	         
	         while (results.next()) {
	        	 find = true;
	        	for(int i=1; i<=numObject; i++)
	        		System.out.print(results.getObject(i)+"\t");
	        	System.out.println();
	         }
	         if(find==false)
	        	 System.out.println("data not found.");
	         if(debug) System.out.println("Successfully finished query");
	     }
	     catch (SQLException e) {
	    	 if(debug) System.out.println("Error running query.");
	    	 if(debug) System.out.println(e.getMessage());    
	     }    
   }
   
	public void brewerySearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		for(int i=0; i<attribute.length; i++) {
			newName = name[i].replaceAll("'", "''");
			if(i==0)
				condition += String.format("%s=='%s'", attribute[i], newName);
			else
				condition += String.format("and %s=='%s'", attribute[i], newName);
		}
		//System.out.println(condition);
		String sql = String.format("SELECT * FROM BREWERY WHERE %s;", condition);
		getResult(sql, 8);
	}
	
	public void beerSearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		for(int i=0; i<attribute.length; i++) {
			newName = name[i].replaceAll("'", "''");
			if(i==0)
				condition += String.format("Brewery.%s=='%s'", attribute[i], newName);
			else
				condition += String.format("and Brewery.%s=='%s'", attribute[i], newName);
		}
		String sql = String.format("SELECT * FROM BEER "
				+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id "
				+ "WHERE %s", condition);
	     getResult(sql, 5);
	}
	
	public void complexSearch(String type, String[] attribute, String[] name) {
		if(type.equals("beer"))
			beerSearch(attribute, name);
		else if(type.equals("brewery"))
			brewerySearch(attribute, name);
	}
	
	public void abvSearch(String tableName, String name) {
		String sql;
		if(tableName.equals("beer"))
			sql = String.format("SELECT BEER.id, BEER.brewery_id, BEER.abv, BEER.name FROM BEER "
					+ "WHERE %s.name = '%s'", tableName, name);
		else 
			sql = String.format("SELECT BEER.id, BEER.brewery_id, BEER.abv, BEER.name FROM BEER "
					+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id "
					+ "WHERE %s.name = '%s'", tableName, name);
		getResult(sql, 4);
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
            	for(int i=1; i<=3; i++)
             		System.out.print(results.getObject(i)+" ");
             	System.out.println();
             	//original output
            	/*beerName = results.getString(1); 
            	beerABV = results.getFloat(2); 
            	breweryName = results.getString(3); 
            	
                System.out.println(beerName + " " + beerABV + " " + breweryName);
                */
             }
             if(debug) System.out.println("Successfully finished beers query");
         }
         catch (SQLException e) {
        	 if(debug) System.out.println("Error running all beers query.");
        	 if(debug) System.out.println(e.getMessage());    
         }    
    }
    
    //Output all breweries 
    //TODO: Add count of beers associated with 
    public void allBreweries(){
   	 String sql = "SELECT b1.id, b1.name, b1.address1, b1.address2,"
    	 		+ " b1.city, b1.state, b1.description, count(*) FROM Brewery b1"
       	 		+ " left join beer on beer.brewery_id == b1.id group by b1.id";

        //String breweryName, address1, address2, city, state, description;

        try (Connection conn = this.connect();
             Statement query  = conn.createStatement();
             ResultSet results    = query.executeQuery(sql)){
            
            // loop through the result set and output in print function
       	 // TODO: Find a cleaner way to printout our data
            while (results.next()) {
            	for(int i=1; i<=8; i++)
            		System.out.print(results.getObject(i)+" ");
            	System.out.println();
            //original output
           	/*breweryName = results.getString(1); 
           	address1 = results.getString(2); 
           	address2 = results.getString(3); 
           	city = results.getString(4); 
           	state = results.getString(5); 
           	description = results.getString(6); 

           	
               System.out.println(breweryName + " " + address1 + " " + address2 + " " + city + "  " + state + " " + description);
               */
            }
            if(debug) System.out.println("Successfully finished breweries query");
        }
        catch (SQLException e) {
       	 if(debug) System.out.println("Error running all breweries query.");
       	 if(debug) System.out.println(e.getMessage());    
        }    
   }
    
    
}