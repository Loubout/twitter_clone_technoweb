package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test; 

import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

import servlet.*;

public class TestFavoriteCommentServlet {

	// set up ServletUnit
	private ServletRunner sr;

	@Before
	public void setup(){
		sr = new ServletRunner();
		sr.registerServlet("AddFavoriteCommentServlet", AddToFavoriteCommentsServlet.class.getName());
		
		
		// to load some initials parameters
	}

	@Test
	public void missingRequiredParameterTest() throws IOException, SAXException, JSONException{
		//set up client
		ServletUnitClient sc = sr.newClient();	
		WebRequest request = new PostMethodWebRequest("http://localhost/comment/addFavorite");
		
		WebResponse response = sc.getResponse(request);
		
		// need to parse response and get JSON
		// then assert error code value
		JSONObject json = new JSONObject(response.getText());
		
		assertEquals(json.get("code"), "12"); // code for parameter error
	}
	
	@Test
	public void wrongParameterTest() throws IOException, SAXException, JSONException{
		//set up client
		ServletUnitClient sc = sr.newClient();	
		WebRequest request = new PostMethodWebRequest("http://localhost/comment/addFavorite");
		
		request.setParameter("userId", "foo");
		request.setParameter("tweetId", "bar");
		
		WebResponse response = sc.getResponse(request);
		// need to parse response and get JSON
		// then assert error code value
		JSONObject json = new JSONObject(response.getText());
				
		assertEquals(json.get("code"), "12"); // code for parameter error
	}
	
	@Test
	public void nonExistingSessionTest() throws IOException, SAXException, JSONException{
		//set up client
		ServletUnitClient sc = sr.newClient();	
		WebRequest request = new PostMethodWebRequest("http://localhost/comment/addFavorite");
		
		
		request.setParameter("key", "12345789"); 
		request.setParameter("tweetId", "14");
		
	
		
		WebResponse response = sc.getResponse(request);
		// need to parse response and get JSON
		// then assert error code value
		JSONObject json = new JSONObject(response.getText());
				
		assertEquals(json.get("code"), "13"); // code for session error
		
	}

}