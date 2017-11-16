package tools;

import org.json.JSONArray;
import org.json.JSONException;

/** Outils pour la manipulation des jSON */

public class JSONTools {
	
	public static JSONArray concatArray(JSONArray... arrs) throws JSONException {
	    JSONArray result = new JSONArray();
	    for (JSONArray arr : arrs) {
	        for (int i = 0; i < arr.length(); i++) {
	            result.put(arr.get(i));
	        }
	    }
	    return result;
	}
}
