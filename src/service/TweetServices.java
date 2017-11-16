package service;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import tools.JSONTools;
import traitement.CommentTraitement;
import traitement.FriendsTraitement;
import traitement.SessionTraitement;
import traitement.UserTraitement;
import error.JSONError;


/**			 Service en rapport avec les tweet :
 * 				 vérification des arguments et convertion
 * 				 création du json 
 * 				 Envoi au traitement approprié */

public class TweetServices {

	public static JSONObject addCommentService(String key, String txt,String color) throws InstantiationException, IllegalAccessException, SQLException, UnknownHostException, MongoException {

		JSONObject json= new JSONObject();
		if (key == null ||txt  == null){
			return new JSONError(12);
		}
		else
		{
			boolean sessionExistCheck = SessionTraitement.checkSessionExists(key);
			if (!sessionExistCheck) return new JSONError(13); 

			boolean sessionDateCheck= SessionTraitement.checkSessionDate(key);
			if (!sessionDateCheck) return new JSONError(9); 


			if(txt.length() > 140)
				return new JSONError(10);

			CommentTraitement.addComment(key, txt,color);
			SessionTraitement.refreshSession(key);

			return json;
		}
	}


	public static JSONObject searchComments(String key, String query, String friends_only) throws InstantiationException, IllegalAccessException, SQLException, UnknownHostException, MongoException, JSONException{
		JSONObject json = new JSONObject();

		/* on sauvegarde la date à laquelle la recherche a été effectuée */
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // je trouvais ça plus easy comme format
		Date date = new Date();
		json.put("date", dateFormat.format(date));


		if (key == null || key == "undefined"){
			// user non auhtentifi� => recherche de tous les tweets dans leur odre chrono
			/* on ajoute le tableau de json contenant tous les tweets */
			json.put("contacts_only", "false"); // on met false par défaut
			json.put("query", "");
			json.put("results", CommentTraitement.findAllComments());
		} else {
			// user authentifie
			boolean sessionExistCheck = SessionTraitement.checkSessionExists(key);
			if (!sessionExistCheck) return new JSONError(13);

			boolean sessionDateCheck= SessionTraitement.checkSessionDate(key);
			if (!sessionDateCheck) return new JSONError(9); 

			/*On sauvegarde l'auteur de la recherche */
			json.put("author", UserTraitement.getUserJSONFormat(key));

			int userId = SessionTraitement.getUserIdFromKey(key);

			int friendsCheck;
			if (friends_only != null){
				friendsCheck = Integer.valueOf(friends_only);		
			} else {
				friendsCheck = 0;
			}
			
			json.put("contacts_only", friendsCheck);

			JSONArray resultsTweets = new JSONArray();
			if (friendsCheck == 1){
				// ne rechercher que les msg des amis
				int[] friendList = FriendsTraitement.getFriends(userId); // checked ça fonctionne
				
		
			
				for (int i: friendList) /* on met tous les tweet des amis dans un tableau */
					resultsTweets = JSONTools.concatArray(resultsTweets, CommentTraitement.findCommentsFromFriend(i));
				// ... j'avais oublié de reaffecter le resultat à resultsTweets
				
			} else {
				/* on utilise une surcharge de findAllComments qui donne l'attribut
				 * contact true/false en comparant l'id de l'auteur et userId
				 */
				resultsTweets = CommentTraitement.findAllComments(userId);
			}

			String trimmedQuery;
			if (query != null) {
				trimmedQuery = query.trim();  // au cas où un mec s'amuse à chercher "    "
			} else {
				trimmedQuery = "";
			}

			/* on sauvegarde la query qui a donné le résultat*/
			json.put("query", trimmedQuery);


			// ATTENTION OMG OMG OMG resultsTweets est un JSONArray qui contient 
			//des PUTAINS DE BASICDBOBject ET PAS DES JSONObject WTF WTF WTF WTF
			// JE DEVIENS FOU ET JE DECHARGE MA RAGE SUR DES COMMENTAIRES QUOI
			// Ca fait 2h que je galère là dessus

			if (trimmedQuery != ""){ // query non nulle
				resultsTweets = CommentTraitement.sortSearchResults(trimmedQuery, resultsTweets);
			} 

			json.put("results", resultsTweets );
		}
		return json;
	}
}