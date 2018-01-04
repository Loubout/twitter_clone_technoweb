package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Manipulation de la base SQL : variables locales */

public class DBStatic {

	private static boolean pooling = true;
	private static String login    = "twitter";
	private static String pwd      = "twitter";
	private static String host     = "127.0.0.1:33306";
	private static String db       = "twitter";
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
