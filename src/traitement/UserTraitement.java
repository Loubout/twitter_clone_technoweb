package traitement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import error.JSONError;
import tools.DBStatic;

/**Traitement des parametres correspondant aux servlets*/
public class UserTraitement {

	public static JSONObject createUserTraitement(String login, String pwd, String firstname, String lastname,String photo,String desc) throws SQLException, JSONException, InstantiationException, IllegalAccessException{

		JSONObject json = new JSONObject();

		if (userExists(login))
			json = new JSONError(18);

		else
			insertUser(login, pwd, firstname, lastname,photo,desc);

		return json;
	}

	public static boolean userExists (String login) throws SQLException, InstantiationException, IllegalAccessException{

		boolean ret = false;
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		/*LOUIS il faut mettre des guillemets ou des ' autour des arguments de where*/

		String query = "select * from users where login='" + login + "';";

		ResultSet rs = st.executeQuery(query);
		if (rs.next())
			ret = true;
		else
			ret = false;

		st.close();
		rs.close();

		return ret;
	}

	public static void insertUser(String login, String pwd , String firstname, String lastname,String photo,String desc) throws SQLException, InstantiationException, IllegalAccessException
	{
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		photo="img/usr"+photo+".jpg";
		String query = "insert into users(login,password,firstname,lastname,photo,description) values('"+login+"','"+pwd+"','"+firstname+"','"+lastname+"','"+photo+"','"+desc+"');";

		st.executeUpdate(query);
		st.close();
	}

	public static int getUserId (String login) throws InstantiationException, IllegalAccessException, SQLException{
		int userId;
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		String query = "select * from users where login = '" + login + "';";
		ResultSet rs = st.executeQuery(query);

		rs.next();
		userId = rs.getInt("id");

		rs.close();
		st.close();

		return userId;
	}
	
	public static boolean checkPassword (String login, String pwd) throws InstantiationException, IllegalAccessException, SQLException{

		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		String query = "select * from users where login = '" + login + "' and password = '"+ pwd + "';";

		ResultSet rs = st.executeQuery(query);

		boolean check = rs.next();

		rs.close();
		st.close();

		return check;
	}

	public static void endUserSession(int userId) throws InstantiationException, IllegalAccessException, SQLException {
		
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();		
		String query = "delete from sessions where id = "+ userId +";";
		st.executeUpdate(query);	
		st.close();
		
	}

	public static String getUserNameById (int id) throws InstantiationException, IllegalAccessException, SQLException{ 
		String userName;
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		String query = "select * from users where id = '"+id+"';";
		ResultSet rs = st.executeQuery(query);

		rs.next();
		userName = rs.getString("id"); /* Cette fonction me parait bien étrange*/

		rs.close();
		st.close();

		return userName;
	}
	public static String getUserIdByLogin (String log) throws InstantiationException, IllegalAccessException, SQLException{ 
		String userId;
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		String query = "select * from users where login = '"+log+"';";
		ResultSet rs = st.executeQuery(query);

		rs.next();
		userId = rs.getString("id");

		rs.close();
		st.close();

		return userId;
	}
	public static String getUserPhotoById  (int id) throws InstantiationException, IllegalAccessException, SQLException{
		/* Ca marche :D */
		String userName;
		Connection c = DBStatic.getConnection();

		Statement st = c.createStatement();

		String query = "select * from users where id = '"+id+"';";
		ResultSet rs = st.executeQuery(query);

		rs.next();
		userName = rs.getString("photo");

		rs.close();
		st.close();

		return userName;
	}
	/* renvoie un objet JSON contenant les infos du user connecté avec la key 
	 * utilisé pour sauvegarder les infos sur l'auteur d'une recherche de tweet */
	public static JSONObject getUserJSONFormat(String key) throws SQLException, InstantiationException, IllegalAccessException, JSONException{
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		String query = "select users.id, users.login from users, sessions where sessions.id = users.id and sessions.sessionKey = '" + key + "';";
		ResultSet rs = st.executeQuery(query);
		
		rs.next();
		String userLogin = rs.getString("login");
		String userId    = rs.getString("id");
		
		JSONObject userJSON = new JSONObject();
		userJSON.put("id", userId);
		userJSON.put("login", userLogin);
		/* pour correspondre à l'objet User javascript
		 * l'auteur d'une recherche n'est pas ami avec lui même => false 
		 */
		userJSON.put("contact" , "false");
		
		return userJSON;
	}

	public static JSONObject getInformation(String id) throws SQLException, InstantiationException, IllegalAccessException, JSONException {
		JSONObject json= new JSONObject();
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		String query = "select * from users where id="+id+" ;";
		ResultSet rs = st.executeQuery(query);
		
		rs.next();
		String userLogin = rs.getString("login");
		String photo = rs.getString("photo");
		String desc = rs.getString("description");
		
		json.put("login", userLogin);
		json.put("photo",photo);
		json.put("desc",desc);
		
		return json;
		
	}

	public static Collection getUserPhotobyId(Object usd) {
		// TODO Auto-generated method stub
		return null;
	}
	


}
