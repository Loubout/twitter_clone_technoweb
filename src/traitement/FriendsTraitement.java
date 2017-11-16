package traitement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import tools.DBStatic;

public class FriendsTraitement {
	
	
	public static void addFriend(String logFriend, String key) throws SQLException, InstantiationException, IllegalAccessException {

		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();		
		/*C'est parti*/
		String idFriend= UserTraitement.getUserIdByLogin (logFriend);
		String query = "insert into friends values( (select id from sessions where sessionKey='"+key+"') ,'"+idFriend+"', NOW())";
		st.executeUpdate(query);	
		st.close();
	}

	public static void removeFriend(int userId, int idFriend ) throws SQLException, InstantiationException, IllegalAccessException {
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();	

		String query = "delete from friends where (id1 = '"+userId + "' and id2 = '"+idFriend +
				"') or (id1 = '"+idFriend + "' and id2 = '"+ userId +"');";
		st.executeUpdate(query);	
		st.close();
	}
	
	public static int[] getFriends (int userId) throws SQLException, InstantiationException, IllegalAccessException{
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();	
		
		String query = "select id1 as idFriend from friends where id2 = '"+userId + "'"
				+ "UNION select id2 as idFriend from friends where id1 = '"+ userId + "';";
		
		ResultSet rs = st.executeQuery(query);	
		
		ArrayList<Integer> friendList = new ArrayList<Integer>();
	
		while (rs.next()){	
			friendList.add(rs.getInt("idFriend"));
		}
		
		rs.close();
		st.close();
		
		int[] friendArray = new int[friendList.size()];
		for (int i = 0; i < friendArray.length; i++) {
		    friendArray[i] = friendList.get(i);
		}
		return friendArray;	
	}
	
	public static boolean areFriends(int id1, int id2) throws InstantiationException, IllegalAccessException, SQLException{
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();	
		
		String query = "select id1, id2 from friends where id1 = '" + id1 + "' and id2 = '" + id2  +"' or"
													+ " id1 = '" + id2 + "' and id2 = '" + id1  + "';";
		
		ResultSet rs = st.executeQuery(query);
		boolean friendCheck = rs.next();
		
		rs.close();
		st.close();
		return friendCheck;
		
	}
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, SQLException{
		int userId = 3, id2 = 8;
		String query = "select id1 as idFriend from friends where id2 = '"+userId + "'"
				+ "UNION select id2 as idFriend from friends where id1 = '"+ userId + "';";
		System.out.println(query);
		
		
	}
}

