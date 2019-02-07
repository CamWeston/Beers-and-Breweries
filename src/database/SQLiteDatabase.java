package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
/**
 *
 * @author Cam Weston
 */
public class SQLiteDatabase {
	
	private final String dbFilePath = "jdbc:sqlite:warmup.db";
	
	
	public SQLiteDatabase(){
		connect();
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
}