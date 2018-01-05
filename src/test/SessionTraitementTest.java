package test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import traitement.SessionTraitement;

public class SessionTraitementTest {

	
	// OCTO
	
	@Before 
	public void setup(){
		// mocking DB
	}
	
	@Test
	public void existingSessionExistCheckShouldBeTrue() throws InstantiationException, IllegalAccessException, SQLException{
		boolean userExists = SessionTraitement.checkSessionExists("eq1t3cbn13pmrt1rekulb7r68r");
		assertTrue(userExists);
	}
	
	@Test
	public void nonExistingSessionExistCheckShouldBeFalse() throws InstantiationException, IllegalAccessException, SQLException{
		boolean userExists = SessionTraitement.checkSessionExists("3fgma8mb8okj12n3ksac4mgn8v");
		assertFalse(userExists);
	}
	
	
	@Test
	public void refreshedSessionShouldDateCheck() throws IllegalAccessException, SQLException, InstantiationException{
		String key = "eq1t3cbn13pmrt1rekulb7r68r";
		boolean dateCheck = false;
		
		SessionTraitement.refreshSession(key);
		boolean datecheck = SessionTraitement.checkSessionDate(key);
		assertTrue(dateCheck);
	}
	
	
}
