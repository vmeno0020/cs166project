/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Food Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql, authorisedUser); break;
                   case 2: UpdateProfile(esql); break;
                   case 3: PlaceOrder(esql); break;
                   case 4: UpdateOrder(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	    String type="Customer";
	    String favItems="";

				 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

  public static void menuItemSearch(Cafe esql) {
    try {
	boolean menuSearch = true;

	while(menuSearch) {
		System.out.println("1. Item Name to search: ");
		System.out.println("2. Item Type to search: ");
		System.out.println("9. Go back.");
		
		switch(readChoice()) {
		case 1: ItemNameSearch(esql); break;
		case 2: ItemTypeSearch(esql); break;
		case 9: menuSearch = false; break;
		default: System.out.println("Invalid Input"); break;
		}
	   }
	}catch(Exception e) {
	   System.err.println (e.getMessage ());
	}
}

  public static void ItemNameSearch(Cafe esql) {
    try {
	System.out.println("Name of Item: ");
	String itemName = in.readLine();
	String nameQuery = String.format("SELECT * FROM Menu WHERE itemName = '%s'", itemName);
	esql.executeQueryAndPrintResult(nameQuery);
	}catch(Exception e){
		System.err.println(e.getMessage ());
	    }
}

  public static void ItemTypeSearch(Cafe esql) {
    try{
	System.out.println("Name of Type: ");        
        String itemType = in.readLine();
        String typeQuery = String.format("SELECT * FROM Menu WHERE type = '%s'", itemType);
        esql.executeQueryAndPrintResult(typeQuery);
        }catch(Exception e){
                System.err.println(e.getMessage ());
		return null;
            }
}

 public static void PrintFullMenu(Cafe esql){
     try{
        System.out.print("========\n FULL MENU \n======"); 
        String query = "SELECT * FROM Menu"; 
        int status = esql.executeQueryAndPrintResult(query); 
        System.out.print("\n\n"); 
     } catch(Exception e){
        System.err.println(e.getMessage()); 
     }
   return null; 

  }

public static void ChangeItem(Cafe esql) {
    try {
	boolean changeMenuItem = true;
	while(changeMenuItem) {
		System.out.print("Type the item name which you want to update.");
		String itemName = in.readLine();
		System.out.println("Choose what attribute of the item you want to update.");
		System.out.println("1. Name");
		System.out.println("2. Description");
		System.out.println("3. Price");
		System.out.println("4. Type");
		System.out.println("5. Image URL");
		System.out.println("9. Go back");

		switch(readChoice()) {
			case 1: System.out.print("\tType the updated item name.");
			String itemName2 = in.readLine();
			String updateNamequery = String.format("UPDATE MENU SET itemName = '%s' WHERE itemName = '%s'", itemName2, itemName);
			esql.executeUpdate(updateNamequery);
			break;

			case 2: System.out.print("\tType the updated item description.");
			String itemDesc2 = in.readLine();
			String updateDescquery = String.format("UPDATE MENU SET description = '%s' WHERE itemName = '%s'", itemDesc2, itemName);
			esql.executeUpdate(updateDescquery);
			break;

			case 3: System.out.print("\tType the updated item price.");
                        String itemPrice2 = in.readLine();
                        String updatePricequery = String.format("UPDATE MENU SET price = '%s' WHERE itemName = '%s'", itemPrice2, itemName);
                        esql.executeUpdate(updatePricequery);
			break;

			case 4: System.out.print("\tType the updated item type.");
                        String itemType2 = in.readLine();
                        String updateTypequery = String.format("UPDATE MENU SET type = '%s' WHERE itemName = '%s'", itemType2, itemName);
                        esql.executeUpdate(updateTypequery);
			break;

			case 5: System.out.print("\tType the updated Image URL of the item.");
                        String itemURL2 = in.readLine();
                        String updateURLquery = String.format("UPDATE MENU SET imageURL = '%s' WHERE itemName = '%s'", itemURL2, itemName);
                        esql.executeUpdate(updateURLquery);
			break;

			case 9: changeMenuItem = false; break;
			default: System.out.println("Invalid input"); break;
		  }
		}
	 }catch(Exception e){
       System.err.println (e.getMessage ());
    }
}
/*
  public static String loginbyManager(Cafe esql) {
   try {
	System.out.println("\tLogin: ");
	String userlogin = in.readLine();
	System.out.print("\tPassword: ");
	String userPassword = in.readLine();
	String Managerquery = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s' AND type = 'Manager'", userlogin, userPassword);
	int loginVal = esql.executeQuery(Managerquery);
	if(loginVal > 0) {
		return userlogin;
		System.out.println("This user is not a manager.");
		return null;
	 }catch(Exception e){
		System.err.println (e.getMessage());
		return null;
	}
}
}
*/
		
public static void Menu(Cafe esql, String authorisedUser){
    try {
	PrintFullMenu(esql);

	  String userType = null;
 	  String query = String.format("SELECT type FROM Users WHERE login='%s'", authorisedUser);
 	  List<List<String>> result = esql.executeQueryAndReturnResult(query);
 	  userType = result.get(0).get(0);
	boolean menu1 = true;


	while(menu1) {
	    if(userType.equals("Customer")) {
		//System.out.println("Cafe Menu:");
		System.out.println("1. View Full Menu ");
		System.out.println("2. Item Search");
		System.out.println("3. Item Type");
		//System.out.println("2. Change Items");
		System.out.println("------------------");
		System.out.println("9. Go Back");
	
		switch (readChoice()) {
		 // calling functions
		 case 1: PrintFullMenu(esql); break;
		 case 2: menuItemSearch(eql); break;
		 case 3: menuTypeSearch(esql); break;
		 //case 2: mUser = loginbyManager(esql); break;
		 case 9: menu1 = false; break;
		 default: System.out.println("Unrecognized choice!"); break;
		}
	 }
		if (userType.equals("Manager")) {
		//	boolean changeMenu = true;
		//	while (changeMenu) {
				System.out.println("1. View Items");
				System.out.println("2. Search for item name");
				System.out.println("3. Search for item type");
				System.out.println("4. Add Items");
				System.out.println("5. Delete Items");
				System.out.println("6. Update Items");
				System.out.println("9. Exit");
				
			switch(readChoice()) {
				case 1: PrintFullMenu(esql); break;
                 		case 2: menuItemSearch(eql); break;
                 		case 3: menuTypeSearch(esql); break;
				case 4: System.out.print("\tAdd the name of the item.");
					String itemName = in.readLine();
					System.out.print("\tAdd the description of the item.");
					String itemDesc = in.readLine();
					System.out.print("\tAdd the price of the item.");
					String itemPrice = in.readLine();
					System.out.print("\tAdd the type of the item.");
					String itemType = in.readLine();
					System.out.print("\t Add the image URL of the item.");
					String itemURL = in.readLine();
					String itemQuery = String.format("INSERT INTO Menu (itemName, description, price, type, imageURL) VALUES ('%s', '%s', '%s', '%s', '%s')", itemName, itemDesc, itemPrice, itemType, itemURL);
					esql.executeUpdate(itemQuery);
					System.out.println("The item has been added.");
				break;
				case 5: System.out.print("Type the item name which you want to delete.");
					String itemName = in.readLine();
					String deleteQuery = String.format("DELETE FROM Menu WHERE itemName='%s'", itemName);
					esql.executeUpdate(deleteQuery);
				break;
				case 6: ChangeItem(esql); break;
				case 9: changeMenu = false; break;
				default: System.out.println("Invalid input"); break;
				}
			}
		
		
	}			 
		
}catch(Exception e){
         System.err.println (e.getMessage ());
	 return null;
	}		
}


 
  public static void UpdateProfile(Cafe esql){
     try{
	System.out.print("\tEnter user login again: ");
	String login = in.readLine();
	System.out.print("\tEnter user password again: ");
	String password = in.readLine();
	boolean updateProf = true;
	
	while(updateProf) {
		System.out.println("1. Update your login.");
		System.out.println("2. Update your phone number.");
		System.out.println("3. Update your password.");
		System.out.println("4. Update your favorite items.");
		System.out.println("5. For managers, update user type.");
		System.out.println("..................................");
		System.out.println("9. Return to the main menu.");
		switch (readChoice()) {
		   case 1: System.out.print("\tEnter your new login: ");
                           String newlogin = in.readLine();
                           String query1 = String.format("UPDATE USERS SET login = '%s' WHERE login = '%s' AND password = '%s'", newlogin, login, password);
                           esql.executeUpdate(query1);
                           System.out.println ("Your login has been updated.");
                           break;
                   case 2: System.out.print("\tEnter your new phone number: ");
                           String newPhoneNum = in.readLine();
                           String query2 = String.format("UPDATE USERS SET phoneNum = '%s' WHERE login = '%s' AND password = '%s'", newPhoneNum, login, password);
                           esql.executeUpdate(query2);
                           System.out.println ("Your phone number has been updated.");
                           break;
                   case 3: System.out.print("\tEnter your new password: ");
                           String newPassword = in.readLine();
                           String query3 = String.format("UPDATE USERS SET password = '%s' WHERE login = '%s' AND password = '%s'", newPassword, login, password);
                           esql.executeUpdate(query3);
                           System.out.println ("Your password has been updated.");
                           break;
                   case 4: System.out.print("\tEnter your new favorite items: ");
                           String newFavItem = in.readLine();
                           String query4 = String.format("UPDATE USERS SET favItems = '%s' WHERE login = '%s' AND password = '%s'", newFavItem, login, password);
                           esql.executeUpdate(query4);
                           System.out.println ("Your favorite items have been updated.");
                           break;
                   case 5: String type="Manager";
                           String query5 = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s' AND type = '%s'", login, password, type);
		           int typeNum = esql.executeQuery(query5);
		           if (typeNum > 0) {
				String usertype = "";
                           	System.out.print("\tEnter your login: ");
			   	String userlogin = in.readLine();
				System.out.println("Choose the user type to update to.");
                              	System.out.println("1. Manager");
                              	System.out.println("2. Employee");
                              	System.out.println("3. Customer");
				switch (readChoice()) {
					case 1: usertype = "Manager"; break;
					case 2: usertype = "Employee"; break;
					case 3: usertype = "Customer"; break;
				default : System.out.println("Unrecognized choice!"); break;
				}
			String query6 = String.format("UPDATE USERS SET type = '%s' WHERE login = '%s'", usertype, userlogin);
			esql.executeUpdate(query6);
			System.out.println("The user type has been updated.");
		}
			else
			    System.out.println("Not a manager, cannot change user type");
			    break;
		case 9: updateProf = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
		}
	}

	}catch(Exception e){
         System.err.println (e.getMessage ());
        // return null;
      }
   }//end
		


  public static void PlaceOrder(Cafe esql, string authorisedUser){
      try{
         PrintFullMenu(esql); 
         //boolean order = true; //come back to later 
            float price = getItemPrice(esql); 
            boolean paid = null; 
            int orderid = getNextOrderID(esql);
            do{
               System.out.println("Would you like to pay now or later?\n1. Now\n2. Later\n"); 
               switch (readChoice()){
                  case 1: paid = true; System.out.println("Paid now.\n"); break; 
                  case 2: paid = false; System.out.println("Paid later.\n"); break; 
                  default: System.out.println("Unrecognized choice!\n"); break; 
               }
            }


            String query = String.format("INSERT INTO Orders (orderid, login, paid, total) VALUES ('%d', '%s', '%b', '%f')", orderid, authorisedUser, paid, price);
            esql.executeQueryAndPrintResult(query);


      } catch(Exception e){
            System.err.println(e.getMessage()); 
         }
   }
  
  public int getNextOrderID(Cafe esql){
     String query = "SELECT MAX(orderid) FROM Orders"; 
     <List<List<String>> res = executeQueryAndReturnResult(query); 
     String currId = res.get(0).get(0); 
     int nextId = parseInt(currId)+1;
     return nextId; 

  }

  public float getItemPrice(Cafe esql){
     do { 
      try{ 
         System.out.print("\nEnter item name: "); 
         String itemNm = in.readLine(); 
         String itemname = String.format("SELECT price FROM Menu WHERE itemName='%s'", itemNm); 
         <List<List<String>>> res = esql.executeQueryAndReturnResult(query); 
         break; 
      }catch(Exception e){
         System.err.println(e.getMessage () ); 
         continue; 
      }

    }while(true); 
    Float price = Float.parseFloat(price.get(0).get(0)); 
    return price; 

  }


}//end Cafe

