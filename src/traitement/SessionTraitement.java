package traitement;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import tools.DBStatic;

public class SessionTraitement {

	private static String generateSessionKey(){
		/* génère une clé de session aléatoire*/
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);		
	}

	public static JSONObject createUserSession(String login, String pwd) throws JSONException {
		/*Création de la clef, de l'objet json et inscription dans la base de données*/
		JSONObject json = new JSONObject();
		json.put("key", generateSessionKey());
		return json;
	}

	public static boolean checkSessionExists (String key) throws SQLException, InstantiationException, IllegalAccessException{
		boolean sessionExists;
		Connection c = DBStatic.getConnection();
	
		Statement st = c.createStatement();
	
		String query = "select * from sessions where sessionKey = '" + key + "';";
		ResultSet rs = st.executeQuery(query);
	
		sessionExists = rs.next();
	
		rs.close();
		st.close();
	
		return sessionExists;
	}

	public static String insertUserSession (int userId, boolean bool) throws InstantiationException, IllegalAccessException, SQLException{
		
		Connection c = DBStatic.getConnection();
	
		Statement st = c.createStatement();
		
		
		String sessionKey = generateSessionKey();
		String query = "insert into sessions values('"+ sessionKey + "','" + userId + "', TIMESTAMP (NOW(), '02:00:00'));";
		
		st.executeUpdate(query);
		st.close();
		
		return sessionKey;
	
	}

	/* Si utilisateur connecté revoi true si date expiré renvoi false 
	 EDIT marche avec clef OU login, ca c'est le talent mon pote*/
	public static boolean checkSessionDate (String arg) throws SQLException, InstantiationException, IllegalAccessException{
		Connection c = DBStatic.getConnection();
	
		Statement st = c.createStatement();
		
		String query = "select TIMESTAMPDIFF(MINUTE, expDate, NOW()) as dateDiff from sessions where id ='" + arg +"' or sessionKey ='"+arg+"';";
		
		System.out.println(query);
		
		ResultSet rs = st.executeQuery(query);
		
		boolean check = false;
		if (rs.next()){
			if (rs.getInt("dateDiff") < 0){
				check = true;
			} 
		}
		rs.close();
		st.close();
	
		return check;
	}

	/*De meme marche avec le login OU la clef*/
	public static void refreshSession(String arg) throws InstantiationException, IllegalAccessException, SQLException {
		
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();
		
		String query = "update sessions set expDate = TIMESTAMPADD (HOUR, 2, NOW()) where id =LEFT('"+arg+"',11) or sessionKey ='"+arg+"' ;";	
		st.executeUpdate(query);
		st.close();
		
	}

	public static int getUserIdFromKey (String key) throws InstantiationException, IllegalAccessException, SQLException{
		int userId;
		Connection c = DBStatic.getConnection();
	
		Statement st = c.createStatement();
	
		String query = "select * from sessions where sessionKey = '" + key + "';";
		
		ResultSet rs = st.executeQuery(query);
		
		rs.next();
		userId = rs.getInt("id");
	
		rs.close();
		st.close();
	
		return userId;
	}

}
