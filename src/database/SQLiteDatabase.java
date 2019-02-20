package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Cam Weston
 * 
 *         In this class we will be connecting to our SQLite database and
 *         executing our queries
 */

public class SQLiteDatabase {
	// If debug == true than output all error messages and control messages
	private final boolean debug = false;
	// Path to beers and breweries database
	private final String dbFilePath = "jdbc:sqlite:src/database/warmup.db";
	// Hashmap of all commands for user. Used in help method
	public Map<String, String> commands = new HashMap<String, String>();

	// Constructor
	public SQLiteDatabase() {
		constructCommands();
	}

	// Hashmap of commands for help
	private void constructCommands() {
		commands.put("exit", "Exit the program");
		commands.put("allbeers", "Return a list of all beers");
		commands.put("allbreweries", "Return a list of all breweries");
		commands.put("brewery beername (beerName)", "Return brewery by a specific beer");
		commands.put("brewery city (cityName)", "Return a list of all breweries by city name");
		commands.put("brewery state (stateName)", "Return a list of all breweries by state name");
		commands.put("brewery city (cityName)	state (stateName)",
				"Return a list of all breweries by city and state name");
		commands.put("brewery desc (any input)",
				"Return a list of breweries with the description containing your input");

		commands.put("beer breweryname (breweryName)", "Return a list of all beers by brewery name");
		commands.put("beer name (breweryName)", "Return a list of all beers by name");
		commands.put("beer state (stateName)", "Return a list of all beers by state name");
		commands.put("beer city (cityName)", "Return a list of all beers by city name");
		commands.put("beer city (cityName) state (stateName)", "Return a list of all beers by city and state name");
		commands.put("beer desc (any input)", "Return a list of beers with the description containing your input");

		commands.put("abv beer (beerName)", "Return abv by beer name");
		commands.put("abv brewery (breweryName)", "Return abv by brewery name");

	}

	// Connection class to connect to our db
	// If not working than check if your dbFilePath string above is correct
	private Connection connect() {
		Connection conn = null;
		try {

			// create a connection to the database
			conn = DriverManager.getConnection(dbFilePath);
			if (debug)
				System.out.println("Connection to beers and breweries database established.");

		} catch (SQLException e) {
			if (debug)
				System.out.println("Could not establish connection.");
			if (debug)
				System.out.println(e.getMessage());
		}
		return conn;
	}

	// Output all commands available to user
	public void help() {
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Command", "Description"));
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = new ArrayList<>();
		for (Map.Entry<String, String> command : commands.entrySet()) {
			row.addAll(Arrays.asList(command.getKey(), command.getValue()));
			rows.add(row);
			row = new ArrayList<>();
		}
		printTable(columns, rows);
	}
	
	//Queries to the DB based upon what is passed into the function
	public void getResult(ArrayList<String> columns, String sql) {
		final int columnCount = columns.size();
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = new ArrayList<>();
		boolean find = false;
		try (Connection conn = this.connect();
				Statement query = conn.createStatement();
				ResultSet results = query.executeQuery(sql)) {
			while (results.next()) {
				find = true;
				for (int i = 1; i <= columnCount; i++)
					row.add(String.valueOf(results.getObject(i)));
				rows.add(row);
				row = new ArrayList<String>();
			}
			if (!find)
				System.out.println("data not found.");
			else {
				printTable(columns, rows);
				if (debug)
					System.out.println("Successfully finished query");
			}
		} catch (SQLException e) {
			if (debug)
				System.out.println("Error running query.");
			if (debug)
				System.out.println(e.getMessage());
		}
	}
	
	
	//Queries based upon a brewery search input (No joins with BEER tables)
	public void brewerySearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList("Name", "Address 1", "Address 2", "City", "State", "Description"));
		
		//Handler if the user is searching by description key words
		if (attribute[0].equals("desc")) {
			for (int i = 0; i < name.length; i++) {
				name[i].trim();
				newName = "%" + name[i] + "%";
				String a = "description";
				if (i == 0)
					condition += String.format("%s LIKE  '%s'", a, newName);
				else
					condition += String.format("and %s LIKE '%s'", a, newName);
			}
			String sql = String.format(
					"SELECT name, address1, address2, city, state, description FROM BREWERY WHERE %s", condition);
			getResult(columns, sql);

		} 
		//Handler for all other cases
		else {
			for (int i = 0; i < attribute.length; i++) {
				newName = name[i].replaceAll("'", "''");
				if (i == 0)
					condition += String.format("%s=='%s'", attribute[i], newName);
				else
					condition += String.format("and %s=='%s'", attribute[i], newName);
			}
			String sql = String.format(
					"SELECT name, address1, address2, city, state, description FROM BREWERY WHERE %s;", condition);
			getResult(columns, sql);
		}
	}
	
	//This function will query for a brewery based upon a beer name 
	public void breweryBeerNameSearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		ArrayList<String> columns = new ArrayList<>(
				Arrays.asList("Name", "Address 1", "Address 2", "City", "State", "Description"));
		for (int i = 0; i < attribute.length; i++) {
			newName = name[i].replaceAll("'", "''");
			if (i == 0)
				condition += String.format("Beer.%s=='%s'", attribute[i], newName);
			else
				condition += String.format("and Beer.%s=='%s'", attribute[i], newName);
		}
		String sql = String.format(
				"SELECT Brewery.name, Brewery.address1, Brewery.address2, Brewery.city, "
				+ "Brewery.state, Brewery.description FROM BREWERY JOIN BEER on Brewery.id == Beer.brewery_id WHERE %s;", condition);
		getResult(columns, sql);
	}
	
	//This function will return a list of beers made by a given brewery
	public void beerBreweryNameSearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Beer Name", "ABV", "Brewery Name"));
		for (int i = 0; i < attribute.length; i++) {
			newName = name[i].replaceAll("'", "''");
			if (i == 0)
				condition += String.format("Brewery.%s=='%s'", attribute[i], newName);
			else
				condition += String.format("and Brewery.%s=='%s'", attribute[i], newName);
		}
		String sql = String.format("SELECT Beer.name, abv, Brewery.name FROM BEER "
				+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id " + "WHERE %s", condition);
		getResult(columns, sql);

	}
	
	//Queries based on beer search input 
	public void beerSearch(String[] attribute, String[] name) {
		String condition = "";
		String newName;
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Name", "ABV", "Description"));
		
		//Handler for if the user is searching by description key words
		if (attribute[0].equals("desc")) {
			for (int i = 0; i < name.length; i++) {
				name[i].trim();
				newName = "%" + name[i] + "%";
				String a = "description";
				if (i == 0)
					condition += String.format("%s LIKE  '%s'", a, newName);
				else
					condition += String.format("and %s LIKE '%s'", a, newName);
			}
			String sql = String.format("SELECT name, abv, description FROM BEER WHERE %s", condition);
			getResult(columns, sql);

		} 
		
		//Handler if the user wants to look by city or state, or both
		//Using JOIN clauses, as we need to see where the beer is brewed (in BREWERY table)
		//All Beers made by all breweries in CITY and/or STATE is queried.
		else if (attribute[0].equals("city") || attribute[0].equals("state")) {
			
			//Special case Handler if the user wants to search by CITY and STATE
			if(attribute.length > 1) {
				if(attribute[1].equals("state")) {
					for (int i = 0; i < attribute.length; i++) {
						name[i].trim();
						newName = name[i].replaceAll("'", "''");
						if (i == 0)
							condition += String.format("Brewery.%s=='%s'", attribute[i], newName);
						else
							condition += String.format("and Brewery.%s=='%s'", attribute[i], newName);
					}
					String sql = String.format(
							"SELECT Beer.name, Beer.abv, Beer.description FROM BEER JOIN BREWERY ON Beer.brewery_id == Brewery.id WHERE %s",
							condition);
					getResult(columns, sql);
				}
				
			}
			
			//generic CITY or STATE case
			else {
				for (int i = 0; i < name.length; i++) {
					name[i].trim();
					newName = name[i].replaceAll("'", "''");
					if (i == 0)
						condition += String.format("Brewery.%s=='%s'", attribute[0], newName);
					else
						condition += String.format("and Brewery.%s=='%s'", attribute[0], newName);
				}
				String sql = String.format(
						"SELECT Beer.name, Beer.abv, Beer.description FROM BEER JOIN BREWERY ON Beer.brewery_id == Brewery.id WHERE %s",
						condition);
				getResult(columns, sql);
			}
		} 
		
		//Handler for all other cases
		else {

			for (int i = 0; i < attribute.length; i++) {
				newName = name[i].replaceAll("'", "''");
				if (i == 0)
					condition += String.format("%s=='%s'", attribute[i], newName);
				else
					condition += String.format("and %s=='%s'", attribute[i], newName);
			}
			String sql = String.format("SELECT name, abv, description FROM BEER " + "WHERE %s", condition);
			getResult(columns, sql);
		}
	}
	
	
	//This function will determine which type of function should be used based on the TYPE string passed in.
	public void complexSearch(String type, String[] attribute, String[] name) {
		if (type.equals("beer"))
			beerSearch(attribute, name);
		else if (type.equals("brewery"))
			brewerySearch(attribute, name);
		else if (type.equals("breweryname")) {
			beerBreweryNameSearch(attribute, name);
		}
		else if(type.equals("beername")) {
			breweryBeerNameSearch(attribute, name);
		}
	}
	
	//Handler for querying beers based on ABV
	public void abvSearch(String tableName, String name) {
		String sql;
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Beer Name", "ABV"));
		if (tableName.equals("beer"))
			sql = String.format(
					"SELECT BEER.id, BEER.brewery_id, BEER.abv, BEER.name FROM BEER " + "WHERE %s.name = '%s'",
					tableName, name);
		else
			sql = String.format(
					"SELECT BEER.id, BEER.brewery_id, BEER.abv, BEER.name FROM BEER "
							+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id " + "WHERE %s.name = '%s'",
					tableName, name);
		getResult(columns, sql);
	}

	/**
	 * @author Cam Weston
	 * 
	 *         Print all beers with there respective breweries
	 */
	public void allBeers() {
		String sql = "SELECT Beer.name, Beer.abv, Brewery.name FROM Beer "
				+ "JOIN BREWERY ON Beer.brewery_id == Brewery.id " + "ORDER BY Brewery.name";
		final int columnCount = 3;
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Beer Name", "ABV", "Brewery Name"));
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = new ArrayList<>();
		try (Connection conn = this.connect();
				Statement query = conn.createStatement();
				ResultSet results = query.executeQuery(sql)) {

			// loop through the result set and output in print function
			while (results.next()) {
				for (int i = 1; i <= columnCount; i++) {
					row.add(String.valueOf(results.getObject(i)));
				}
				rows.add(row);
				row = new ArrayList<String>();
			}
			printTable(columns, rows);
			if (debug)
				System.out.println("Successfully finished beers query");
		} catch (SQLException e) {
			if (debug)
				System.out.println("Error running all beers query.");
			if (debug)
				System.out.println(e.getMessage());
		}
	}

	/**
	 * @author Cam Weston
	 * 
	 *         Print all breweries
	 * 
	 */
	public void allBreweries() {

		String sql = "SELECT b1.name, b1.address1, b1.address2,"
				+ " b1.city, b1.state, b1.description, count(*) FROM Brewery b1"
				+ " left join beer on beer.brewery_id == b1.id group by b1.id";

		final int columnCount = 7;
		ArrayList<String> columns = new ArrayList<>(Arrays.asList("Brewery Name", "Address 1", "Address 2", "City",
				"State", "Description", "Number of Beers"));
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = new ArrayList<>();

		try (Connection conn = this.connect();
				Statement query = conn.createStatement();
				ResultSet results = query.executeQuery(sql)) {

			// loop through the result set and output in print function
			while (results.next()) {
				for (int i = 1; i <= columnCount; i++)
					row.add(String.valueOf(results.getObject(i)));
				rows.add(row);
				row = new ArrayList<String>();
			}
			printTable(columns, rows);
			if (debug)
				System.out.println("Successfully finished breweries query");
		} catch (SQLException e) {
			if (debug)
				System.out.println("Error running all breweries query.");
			if (debug)
				System.out.println(e.getMessage());
		}
	}

	/**
	 * @author Cam Weston
	 * 
	 *         Dynamically create and print a table for the given data
	 * 
	 *         Pass in the column names and table rows and create a dynamically
	 *         sized output table
	 */
	private void printTable(ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
		try {
			// Iterate through rows and get longest in each to column to see how big to set
			// your tab space for printf
			ArrayList<String> columnLengths = new ArrayList<>();
			int currentColumn = 0;
			int hyphensToPrint = 0;
			// Start max at name of column because column data length may be less than
			// column name ie ("ABV" = 3 and "1" = 1)
			int max = columns.get(currentColumn).length();
			for (int i = 0; i < columns.size(); i++) {
				for (int j = 0; j < rows.size(); j++) {
					if (rows.get(j).get(i).length() > max) {
						max = rows.get(j).get(i).length();
					}
				}
				hyphensToPrint += max + 5;
				// Create format string and add 5 to max column width to evenly space things
				columnLengths.add(new String("%-" + String.valueOf(max + 5) + "s"));
				// Only reassign our max column width while still in index bounds
				if (currentColumn < columns.size() - 1) {
					currentColumn += 1;
					max = columns.get(currentColumn).length();
				}
			}
			// Print hyphens for roof of table
			for (int i = 0; i < hyphensToPrint; i++) {
				System.out.print("-");
			}
			System.out.println();

			// Print each column name
			for (int i = 0; i < columnLengths.size(); i++) {
				System.out.printf(columnLengths.get(i), columns.get(i));
			}
			System.out.println();

			// Print hyphens for floor of columns
			for (int i = 0; i < hyphensToPrint; i++) {
				System.out.print("-");
			}
			System.out.println();

			// Print each row using the right column length
			for (ArrayList<String> row : rows) {
				for (int i = 0; i < row.size(); i++) {
					System.out.printf(columnLengths.get(i), row.get(i));
				}
				System.out.println();
			}
			if (debug)
				System.out.println("Finished printing table");
		} catch (Exception e) {
			System.out.println("Failed to print table");
			if (debug)
				System.out.println(e);
		}
	}

	/**
	 * @author Cam Weston Test print table function
	 */
	private void testPrintTable() {
		ArrayList<String> testColumns = new ArrayList<>(Arrays.asList("Test1", "Test2", "Test3"));
		ArrayList<ArrayList<String>> testRows = new ArrayList<>();
		ArrayList<String> testRow1 = new ArrayList<>(Arrays.asList("baddadbing", "less", "fofossdsdsdfsdfsdfsfs"));
		ArrayList<String> testRow2 = new ArrayList<>(Arrays.asList("babing", "lss", "sdsdsdfsdfsdfsfs"));
		ArrayList<String> testRow3 = new ArrayList<>(Arrays.asList("baddadbing", "less", "fofossdsdsdfsdfsdfsfs"));
		testRows.addAll(Arrays.asList(testRow1, testRow2, testRow3));
		printTable(testColumns, testRows);
	}

}