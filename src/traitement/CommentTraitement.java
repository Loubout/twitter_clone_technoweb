package traitement;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import tools.DBStatic;
import tools.MongoDBStatic;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoException;

public class CommentTraitement {

	public static void addComment (String key, String txt,String color) throws UnknownHostException, MongoException, SQLException, InstantiationException, IllegalAccessException{

		DBCollection coll = MongoDBStatic.getMongoConnection("comments");

		BasicDBObject dbComment= new BasicDBObject();

		/*Recupperation des infos de l'auteur */
		BasicDBObject authorDb = new BasicDBObject();
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		/* on chope le login et l'id */
		String query = "select users.id as authorId, users.login as authorLogin "
				+ "from users, sessions "
				+ "where sessions.sessionKey = '" + key + "' and users.id = sessions.id;";
		
		ResultSet rs = st.executeQuery(query);
		rs.next();

		/* on stocke le tout dans deux variables */
		String authorId = rs.getString("authorId");
		String authorLogin = rs.getString("authorLogin");

		rs.close();
		st.close();

		/* puis dans un json */
		authorDb.put("id", authorId);
		authorDb.put("login", authorLogin);
		
		
		/*on met ce json dans l'objet final */
		dbComment.put("author", authorDb);

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // je trouvais ça plus easy comme format
		Date date = new Date();

		dbComment.put("date", dateFormat.format(date));

		//	BasicDBObject authorInfo = new BasicDBObject(); // faut faire des nouvelles requetes et tout
		//	dbComment.put("authorId", String.valueOf(userId)); // à remplacer par toutes les infos du user eventuellement

		dbComment.put("text", txt);		
		
		/*Apres coup : photo et  couleur */
		dbComment.put("photo",UserTraitement.getUserPhotoById(Integer.parseInt(authorId)));
		dbComment.put("bgColor",color);


		coll.insert(dbComment);// ca marche mtn c'�tait juste pas le bon port pour mongo...


	}

	/* renvoie un jsonArray contenant tous les tweets */
	public static JSONArray findAllComments() throws UnknownHostException, MongoException, JSONException{

		DBCollection com =  MongoDBStatic.getMongoConnection("comments");
		DBCursor cursor = com.find();

		JSONArray jsonRes = new JSONArray();
		//List<DBObject> tweetList = cursor.toArray();
		while (cursor.hasNext()) {
			jsonRes.put(cursor.next());
		}
		return jsonRes;
	}

	/* renvoie un jsonArray contenant tous les tweets en précisant si leur auteur est un ami de l'utilisateur */
	public static JSONArray findAllComments(int userId) throws UnknownHostException, MongoException, JSONException, InstantiationException, IllegalAccessException, SQLException {
		// TODO Auto-generated method stub
		DBCollection com =  MongoDBStatic.getMongoConnection("comments");
		DBCursor cursor = com.find();

		JSONArray jsonResult = new JSONArray();
		boolean checkFriendship;
		BasicDBObject currTweetAuthor;
		while (cursor.hasNext()) {
			DBObject currTweet = cursor.next(); // tweet en cours de traitement
			currTweetAuthor = (BasicDBObject) currTweet.get("author"); // l'objet author du tweet

			/* test de la relation d'amitié */
			checkFriendship = FriendsTraitement.areFriends(userId, Integer.parseInt((String) currTweetAuthor.get("id"))); 

			/* on precise l'existence de la relation d'amitié par rapport à l'utilisateur */
			currTweetAuthor.put("contact", checkFriendship);

			/* modification de l'objet author puis reinjection */
			currTweet.put("author", currTweetAuthor);
			jsonResult.put(currTweet); // ajout au tableau
		}
		return jsonResult;
	}

	public static JSONArray findCommentsFromFriend (int userId) throws UnknownHostException, MongoException, JSONException{
		DBCollection com =  MongoDBStatic.getMongoConnection("comments");
		BasicDBObject query = new BasicDBObject();
		
		
		//   http://docs.mongodb.org/manual/reference/method/db.collection.find/
		// attention à bien passer userId en String
		query.put("author.id", String.valueOf(userId)); 
		DBCursor cursor = com.find(query);

		JSONArray jsonResult = new JSONArray();
		/* comme le cursor renvoie des DBObject c'est ça qu'on insère dans le JSONArray d'où mon com
		 * de rage intense dans findCommentService
		 */
		BasicDBObject currTweetAuthor;
		while (cursor.hasNext()) {
			DBObject currTweet = cursor.next(); // tweet en cours de traitement
			currTweetAuthor = (BasicDBObject) currTweet.get("author"); // l'objet author du tweet
			
			currTweetAuthor.put("contact", true); // à priori on sait déjà que c'est un ami
			currTweet.put("author", currTweetAuthor);
			jsonResult.put(currTweet);
		}
		return jsonResult;
	}

	/* Prend en argument une query de recherche et un ensemble de résultat et donne à chaque tweet
	 * un score de pertinence par rapport à la query
	 * JE SAIS PLUS DU TOUT CE QUE CA FAIT EN VRAI JE SUIS PAUME FAUT RELIRE LE COURS
	 */
	public static JSONArray sortSearchResults(String query, JSONArray results) throws InstantiationException, IllegalAccessException, SQLException, JSONException{
		// nombre de tweet à traiter
		int nDoc = results.length();
		// on pourrait gérer les '+' aussi en mettant une ptit regex mais fuck it pour le moment
		String[] queryWords = query.split(" ");

		// je trouve pas de methode pour supprimer un élément du JSONArray ... du coup je vais en reconstruire un
		// normalement on devrait pouvoir juste virer les tweets qui ont une pertinence nulle
		//JSONArray sortedResults = new JSONArray();
		double curTweetScore;
		JSONArray sortedResults = new JSONArray();
		for (int i = 0; i < nDoc; i++){

			BasicDBObject curTweet = (BasicDBObject) results.get(i);
			curTweetScore = calculateRSV(curTweet, queryWords, nDoc);
			if (curTweetScore != 0){
				curTweet.put("score", curTweetScore);
				sortedResults.put(curTweet); // on garde que les tweets dont le score est positif
			}
		}
		return sortedResults;
	}

	/* Affecte une valeur de score de pertinence à un tweet (document) parmi nDoc tweet
	 *  d'après la une query constituée d'une liste de mot */
	public static double calculateRSV (BasicDBObject document, String[] queryWords, int nDoc) throws JSONException, SQLException, InstantiationException, IllegalAccessException{

		String documentId = document.getString("_id");
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		/* DEAL WITH IT */
		String query = "select SUM(tf_idf) as RSV from( " +
				"select (idf * tf) as tf_idf, word from ( " +
				"select termfrequency.word, LOG(" + nDoc  +"/ occurence) as idf, frequency as tf " +
				"from docfrequency " + 
				"inner join termfrequency on docfrequency.word = termfrequency.word " +
				"where termfrequency.documentId = '" + documentId + "') as tweet_data " +
				"where ";

		// on ajuste la requete avec les mots de la query
		for (String word : queryWords ){
			query += "word = '" + word + "' or ";
		}
		// pour enlever le dernier "or " qui est inevitable
		query = query.substring(0, query.length() - 3) + ") as tf_idf_table;";


		// retrieving RSV value
		ResultSet rs = st.executeQuery(query);
		double scoreRSV;

		rs.next();
		scoreRSV = rs.getDouble("RSV");

		st.close();
		c.close();

		return scoreRSV;

	}


	/* parcourt tous les tweets et insère dans la table SQL termfrequency un nombre d'occurences 
	 * pour chaque couple (mot, document)
	 */
	public static void setTermFrequencies() throws UnknownHostException, MongoException, InstantiationException, IllegalAccessException, SQLException {
		DBCollection coll = MongoDBStatic.getMongoConnection("comments");

		/* Map -> Sort un couple (Mot->Document)*/
		String m = "function() {" +
				"var words = this.text.match(/\\w+/g);"+
				"var wordCounts  = [];"+
				"if (words == null){"+
				"return;"+
				"}"+
				"for(var i = 0; i < words.length ; i++){"+
				"if (wordCounts.indexOf(words[i]) == -1){"+
				"wordCounts[words[i]] = 1;" +
				"} else {"+
				"wordCounts[words[i]] = wordCounts[words[i]] + 1;" +
				"}"+
				"}"+
				"for(var i = 0; i < words.length ; i++){" +
				"emit( { word: words[i], document_id: this._id }, {count : wordCounts[words[i]]});" +  // plutot un truc du genre emit({words[i], this._id}, {tf : wordCounts[words[i]]}
				"}" +
				"}";

		/* Reduce -> Sort une liste (mot->NbDocument) */
		String r = "function(key, values){"+
				"var reducedValue = 0; "+
				"var k = key; " +
				"for (var i = 0; i < values.length; i++) {"+
				"reducedValue += values[i].count; " +
				"}"+
				"return {count : reducedValue};"+
				"}";

		MapReduceOutput out = coll.mapReduce(m, r, null, MapReduceCommand.OutputType.INLINE, null);

		for (DBObject obj : out.results()){
			BasicDBObject basicObj = (BasicDBObject)obj;

			// exemple de obj 
			//{ "_id" : { "word" : "Bande" , "document_id" : { "$oid" : "55439454e4b05b80282662e3"}} , "value" : { "count" : 1.0}}

			/* retrieving term */
			BasicDBObject obj_id = (BasicDBObject)basicObj.get("_id");
			String term = obj_id.getString("word");

			/* retrieving tweet mongo id */
			ObjectId obj_documentId = (ObjectId)obj_id.get("document_id");

			String documentId = obj_documentId.toStringMongod();

			/* retrieving term count in that particular tweet */
			BasicDBObject obj_value = (BasicDBObject)basicObj.get("value");
			Double termFrequency        = obj_value.getDouble("count");

			upsertTermFrequency(term, documentId,  termFrequency);
		}	
	}

	/* parcourt tous les tweets et insère dans la table SQL docfrequency pour la chaque mot
	 * le nombre de document (tweets) dans lequel il apparaît
	 */
	public static void setDocumentFrequencies() throws UnknownHostException, MongoException, InstantiationException, IllegalAccessException, SQLException {	
		DBCollection coll = MongoDBStatic.getMongoConnection("comments");

		/* Map -> Sort un couple (Mot->Document)*/
		String m = "function() { " +
				"var words = this.text.match(/\\w+/g); "+ // ouais et là fallait ecrire this.text et pas this.content .. fuck
				"var emitted = []; " +
				"if (words == null){ " +
				"return; " +
				"} " +
				"for(var i = 0; i < words.length; i++) { "+
				"if (emitted.indexOf(words[i]) == -1) { "+
				"emit(words[i], {df : 1}); " +    
				"emitted.push(words[i]); " +
				"} "+
				"} "+
				"} ";

		/*  IMPORANT : il faut que la valeur que tu renvoies sur le reduce ait la même forme 
		 *  que la valeur que tu 'emit' dans le map 
		 *  Du coup on met la forme {df : <un nombre>} sur le emit du map et pareil sur le return du reduce.
		 *  Ca assure que tous les résultats aient la même forme. Avant on avait juste emit (words[i], 1)
		 *  et j'avais tout le temps en même temps des "value" : "1.0" ou bien des "value" : {"key" : "2.0"}
		 *  En s'assurant d'avoir la même forme partout on a des résultats homogènes et on peut le parser 
		 *  en utilisant toujours la même instruction plutôt qu'en testant à chaque fois. si la clé value est
		 *  une valeur ou un objet. Cimer Stackoverflow quoi..
		 */

		/* Reduce -> Sort une liste (mot->NbDocument) */
		String r = "function(key, values){ "+
				"var total = 0; "+
				"for (var i in values) { "+
				"total += 1 " +  // this seems to do the job 
				"} " +
				"return {df : total}; " + // OH MY GOD FAUT METTRE ':' ET PAS ',' IM SO MAD 
				"} ";


		MapReduceCommand cmd = new MapReduceCommand(coll, m, r, null, MapReduceCommand.OutputType.INLINE, null);  

		MapReduceOutput out =  coll.mapReduce(cmd);  
		for (DBObject obj : out.results()){
			/* on fiat des cast en BasicDBObject ça nous permet d'utiliser les methodes genre 
			 * getDouble  getString etc .. c'est plus propre */
			BasicDBObject basicObj = (BasicDBObject)obj;
			String term =  basicObj.getString("_id");
			BasicDBObject value = (BasicDBObject)basicObj.get("value");
			double docfrequency = value.getDouble("df");

			upsertDocumentFrequency(term, docfrequency);
		}
	}


	public static void upsertDocumentFrequency(String term, double freq) throws SQLException, InstantiationException, IllegalAccessException{
		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		/*la meme idée que pour upsertTermFrequencies */
		String query = "INSERT INTO docfrequency "+
				"VALUES('" + term + "', '"+ freq + "') "+
				"ON DUPLICATE KEY UPDATE " +
				"occurence = '" + freq + "';";

		System.out.println(query);
		st.executeUpdate(query);
		st.close();
	}


	public static void upsertTermFrequency(String term, String document_id,  double freq) throws InstantiationException, IllegalAccessException, SQLException{

		Connection c = DBStatic.getConnection();
		Statement st = c.createStatement();

		/*Pas au point*/
		//String query = "update termfrequency set frequency='"+freq+"' where word='"+term+"'or insert into termfrequency values('"+term+"','"+freq+"' ;";
		/*1ere soltution en glanant sur google ça a l'air pas trop mal */
		String query = "INSERT INTO termfrequency "+
				"VALUES('" + term + "', '"+ document_id + "', '" + freq + "') "+
				"ON DUPLICATE KEY UPDATE " +
				"frequency = '" + freq + "';";

		st.executeUpdate(query);
		st.close();
	}

	public static void main(String[] args) throws UnknownHostException, MongoException, InstantiationException, IllegalAccessException, SQLException{

	}

}