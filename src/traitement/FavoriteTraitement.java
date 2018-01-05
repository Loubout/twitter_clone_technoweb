package traitement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import tools.DBStatic;

public class FavoriteTraitement {

	
	public static void addToFavoriteTweets(int userId, int tweetId) throws InstantiationException, IllegalAccessException, SQLException{
		
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();		
		
		// to continue ...
		String query = "insert into friends values"; 
	}
}
