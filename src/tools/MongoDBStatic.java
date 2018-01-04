package tools;

import java.net.UnknownHostException;

import com.mongodb.Mongo ;
import com.mongodb.DB;
import com.mongodb.DBCollection ;
import com.mongodb.BasicDBObject ;
import com.mongodb.MongoException;

/** Manipulation de la base MongoDB */

public class MongoDBStatic {
	private final static String url = "127.0.0.1";
	private final static int   port = 27017;
	private final static String  db = "gr1_bout_fran";

	
	public static DBCollection getMongoConnection(String colName) throws UnknownHostException, MongoException{
		Mongo m = new Mongo (url, port);
		DB dbase = m.getDB (db);
		DBCollection col = dbase.getCollection(colName);	
		return col;
	}
	
	
	public static void insertCommentMongoDB (BasicDBObject obj) throws UnknownHostException, MongoException{	
		DBCollection col = getMongoConnection("comments");
		col.insert(obj);		
	}
	
	
	
	

}
