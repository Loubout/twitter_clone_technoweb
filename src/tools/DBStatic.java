package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Manipulation de la base SQL : variables locales */

public class DBStatic {

	private static boolean pooling = true;
	private static String login    = "gr1_bout_fran";
	private static String pwd      = "gr1_bout_fran$";
	private static String host     = "132.227.201.129:33306";
	private static String db       = "gr1_bout_fran";
	private static Database database;

	
	public static Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException
	{

		if (pooling == false)
		{
			return (DriverManager.getConnection("jdbc:mysql;//" + DBStatic.host
					+"/"
					+DBStatic.db, DBStatic.login, DBStatic.pwd));
		}
		else
		{
			if (database == null)
			{
				database = new Database("jdbc/db");
			}

			return database.getConnection();
		}					
	}





}
