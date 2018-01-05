package service;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import error.JSONError;
import service.exception.MissingRequiredParameterException;
import service.exception.NonExistingSessionException;
import service.exception.NonExistingTweetException;
import service.exception.WrongFormatIdException;
import service.exception.WrongFormatSessionKeyException;
import traitement.CommentTraitement;
import traitement.FavoriteTraitement;
import traitement.FriendsTraitement;
import traitement.SessionTraitement;
import traitement.UserTraitement;

/**			 Service en rapport avec les utilisateurs :
 * 				 vérification des arguments et convertion
 * 				 création du json 
 * 				 Envoi au traitement approprié */

public class UserServices {

	public static JSONObject createUserService(String login, String pwd, String firstname, String lastname,String photo,String desc) throws JSONException, SQLException, InstantiationException, IllegalAccessException {

		/*Objet json*/
		JSONObject json = new JSONObject() ;

		/*Vérification des parametres*/
		if (login == null ||pwd   == null ||firstname == null|| lastname == null){
			return new JSONError(12);
		}
		else{
			synchronized("create"){
				json = UserTraitement.createUserTraitement(login, pwd, firstname, lastname,photo,desc);	
			}
		}
		return json;
	}

	public static JSONObject loginUserService(String login, String pwd) throws JSONException, InstantiationException, IllegalAccessException, SQLException {

		if (login == null ||pwd   == null){
			return new JSONError(12);
		}

		synchronized("login"){

			JSONObject json = new JSONObject();

			boolean isUser = UserTraitement.userExists(login);
			boolean pwdOK  = UserTraitement.checkPassword(login, pwd);

			if (!isUser || !pwdOK) 
				return new JSONError(12);


			int userId = UserTraitement.getUserId (login);

			UserTraitement.endUserSession(userId);
			String sessionKey = SessionTraitement.insertUserSession (userId, false);

			json.put("key", sessionKey);
			json.put("id", userId);
			json.put("login", login);
			json.put("photo", UserTraitement.getUserPhotoById (userId));
			
			int[] idFriend = FriendsTraitement.getFriends (userId);
			for (int id : idFriend){
				json.append("friends",UserTraitement.getUserNameById (id) ); /* Le numéro id */
			}		
			return json;

		}
	}



	public static JSONObject logoutUserService(String login, String pwd) throws JSONException, InstantiationException, IllegalAccessException, SQLException {
		JSONObject json = new JSONObject() ;

		/*Vérification des parametres*/
		if (login == null )return new JSONError(12);


		synchronized("logout"){
			int userId = UserTraitement.getUserId (login);
			UserTraitement.endUserSession(userId);	
		}

		return json;
	}

	public static JSONObject addFriendService(String loginFriend, String key) throws InstantiationException, IllegalAccessException, SQLException {
		/*Vérification des parametres*/
		if (key == null ||loginFriend   == null)
			return new JSONError(12);

		/*Verification de la connexion de l'utilisateur */
		if(SessionTraitement.checkSessionDate (key) == false)
			return new JSONError(13);


		/*Tout est bon, on raffraichi la session et on lance le traitement*/
		JSONObject json = new JSONObject() ;
		SessionTraitement.refreshSession(key);
		FriendsTraitement.addFriend(loginFriend, key);
		return json;
	}


	public static JSONObject removeFriendService(String idFriend, String key) throws InstantiationException, IllegalAccessException, SQLException {
		if (key == null ||idFriend   == null)	
			return new JSONError(12);

		/*Verification de la connexion de l'utilisateur */
		if(SessionTraitement.checkSessionDate (key) == false)
			return new JSONError(13);

		JSONObject json = new JSONObject() ;
		SessionTraitement.refreshSession(key);

		int userId = SessionTraitement.getUserIdFromKey(key);
		FriendsTraitement.removeFriend(userId, Integer.parseInt(idFriend));
		return json;
	}
	

	public static JSONObject getInformations(String ids) throws JSONException, InstantiationException, IllegalAccessException, SQLException {

		if(ids == null)
			return new JSONError(12);

		/*On sépare les différents id recues */
		String[] tabIds = ids.split(",");

		/* Le retour*/
		JSONObject json = new JSONObject() ;

		/* On les traite*/
		for(String id : tabIds){
			json.put(id,UserTraitement.getInformation(id));
		}

		return json;
	}
	
	
	// OCTO
	
	public static JSONObject addCommentToFavoritesService(String tweetId, String key) throws InstantiationException, IllegalAccessException, SQLException, MissingRequiredParameterException, NonExistingSessionException, WrongFormatSessionKeyException, WrongFormatIdException, NonExistingTweetException{
		if (key == null || tweetId == null){
			throw new MissingRequiredParameterException();
		}
		
		if (key.length() != 32){
			throw new WrongFormatSessionKeyException();
		}
	
		if(SessionTraitement.checkSessionExists(key) == false){
			throw new NonExistingSessionException();
		}
		
		int parsedTweetId;
		
		try{
			parsedTweetId = Integer.parseInt(tweetId);
		}catch(NumberFormatException e){
			throw new WrongFormatIdException();
		}
		
		if(parsedTweetId < 0){
			throw new WrongFormatIdException();
		}
		
		if (!CommentTraitement.checkCommentExists(parsedTweetId)){
			throw new NonExistingTweetException();
		}
		
		int userId = SessionTraitement.getUserIdFromKey(key);
		
		FavoriteTraitement.addToFavoriteTweets(userId, parsedTweetId);
		
		SessionTraitement.refreshSession(key);
		JSONObject json = new JSONObject() ;
		
		
		
		// to continue ..
		return null;

	}


}


