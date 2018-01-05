package test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import service.UserServices;
import service.exception.MissingRequiredParameterException;
import service.exception.NonExistingSessionException;
import service.exception.NonExistingTweetException;
import service.exception.WrongFormatIdException;
import service.exception.WrongFormatSessionKeyException;
import traitement.SessionTraitement;

public class UserServicesTest {
	
	// OCTO
		
	@Before
	public void setup(){
		// need to mock DB here
	}
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void missingIdParameterShouldFail() throws InstantiationException, IllegalAccessException, SQLException, MissingRequiredParameterException{
		String tweetId = null;
		String key = "hello";
		
		exception.expect(MissingRequiredParameterException.class);
		// exception.expectMessage ...
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	@Test
	public void missingKeyParameterShouldFail() throws InstantiationException, IllegalAccessException, SQLException{
		String tweetId = null;
		String key = "hello";
		
		exception.expect(MissingRequiredParameterException.class);
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	
	@Test
	public void wrongFormatSessionKeyShouldFail() throws InstantiationException, IllegalAccessException, SQLException{ 
		//	addCommentToFavoritesService (String tweetId, String key)
		String tweetId = "12";
		String key = "hello";
		
		exception.expect(WrongFormatSessionKeyException.class);
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	@Test
	public void nonPositiveIntTweetIdShouldFail() throws InstantiationException, IllegalAccessException, SQLException{
		String tweetId = "hi";
		String key = "eq1t3cbn13pmrt1rekulb7r68r";
	
		
		exception.expect(WrongFormatIdException.class);
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	
	@Test
	public void nonExistingSessionShouldFail() throws InstantiationException, IllegalAccessException, SQLException{
		String tweetId = "12";
		String key = "3fgma8mb8okj12n3ksac4mgn8v";
		
		exception.expect(NonExistingSessionException.class);
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	@Test 
	public void nonExistingTweetShouldFail() throws InstantiationException, IllegalAccessException, SQLException{
		String tweetId = "1948957397193828";
		String key = "eq1t3cbn13pmrt1rekulb7r68r";
	
		
		exception.expect(NonExistingTweetException.class);
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
	}
	
	
	@Test void activeSessionShouldSucceed() throws InstantiationException, IllegalAccessException, SQLException{
		String tweetId = "12";
		String key = "eq1t3cbn13pmrt1rekulb7r68r";
		JSONObject json = UserServices.addCommentToFavoritesService(tweetId, key);
		assertTrue(SessionTraitement.checkSessionExists(key));
	}
	
	
	
}
