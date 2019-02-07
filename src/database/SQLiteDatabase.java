package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
 
/**
 *
 * @author Cam Weston
 */
public class SQLiteDatabase {
	
	private final String dbFilePath = "jdbc:sqlite:warmup.db";
	private Dictionary<String,String> commands = new Hashtable<String,String> ();

	
	public SQLiteDatabase(){
		constructCommands();
		connect();
	}
	
	private void constructCommands(){
		commands.put("exit", "Exit the program");
		commands.put("beers", "Return a list of all beers");
		commands.put("breweries", "Return a list of all breweries");
	}
	
	private String getDbFilePath(){
		return dbFilePath;
	}
	
    private void connect() {
        Connection conn = null;
        try {
      
            // create a connection to the database
            conn = DriverManager.getConnection(getDbFilePath());
            
            System.out.println("Connection to beers and breweries database established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    public void help(){
    	System.out.println("Commands:");
    	Enumeration<String> keys = commands.keys();
    	while(keys.hasMoreElements()){
    		System.out.println(keys.nextElement());
    	}
    }
}