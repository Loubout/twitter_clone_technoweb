package error;

import org.json.JSONException;
import org.json.JSONObject;


/**Gestion des erreurs */
public class JSONError extends JSONObject {

	JSONObject j;

	public String toString(){
		return j.toString();
	}

	public JSONError(int code){
		j = new JSONObject();
		switch(code){

		case 12:
			try {
				j.put("code","12");
				j.put("message","Probleme d'arguments");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;

		case 13:
			try {
				j.put("code","13");
				j.put("message","Probleme session inexistante");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;

		case 14:
			try {
				j.put("code","14");
				j.put("message","Probleme d'instanciation");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;

		case 15:
			try {
				j.put("code","15");
				j.put("message","probleme SQL");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;
		case 16:
			try {
				j.put("code","16");
				j.put("message","Probleme d'accès illegal");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;
		case 17:
			try {
				j.put("code","17");
				j.put("message","Probleme MongoDB");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;
		case 18:
			try {
				j.put("code","18");
				j.put("message","Utilisateur/Session déja existant(e)");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;

		default:
			try {
				j.put("404","Error not found");
			} catch (JSONException e) {
				e.printStackTrace();
			}break;

		}

	}

}
