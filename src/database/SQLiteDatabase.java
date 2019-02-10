package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
 
/**
 *
 * @author Cam Weston
 */
public class SQLiteDatabase {
	
	private final String dbFilePath = "jdbc:sqlite:warmup.db";
	public Map<String,String> commands = new HashMap<String,String> ();

	
	public SQLiteDatabase(){
		constructCommands();
	}
	
	private void constructCommands(){
		commands.put("exit", "Exit the program");
		commands.put("beers", "Return a list of all beers");
		commands.put("breweries", "Return a list of all breweries");
	}
	
    private Connection connect() {
        Connection conn = null;
        try {
      
            // create a connection to the database
            conn = DriverManager.getConnection(dbFilePath);
            
            System.out.println("Connection to beers and breweries database established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
    }
    
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
    
    public void help(){
    	for(Map.Entry<String,String> command : commands.entrySet()){
    		System.out.println(command.getKey() + " : " + command.getValue());
    	}
    }
    
    public void allBeers(){
    	 String sql = "SELECT Beer.name, Beer.abv, Beer.description, Brewery.name FROM Beer "
    	 			+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id "
    	 			+ "ORDER BY Brewery.name";

         
         try (Connection conn = this.connect();
              Statement query  = conn.createStatement();
              ResultSet results    = query.executeQuery(sql)){
             
             // loop through the result set
             while (results.next()) {
                 System.out.println(results);
             }
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
    }
}