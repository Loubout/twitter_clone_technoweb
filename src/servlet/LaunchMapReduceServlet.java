package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.MongoException;

import error.JSONError;
import traitement.CommentTraitement;

/**
 * Servlet implementation class LaunchMapReduceServlet
 */

public class LaunchMapReduceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		try {
			CommentTraitement.setDocumentFrequencies();
			CommentTraitement.setTermFrequencies();
		} catch (InstantiationException e) {
			json = new JSONError(14);
		} catch (IllegalAccessException e) {
			json = new JSONError(16);
		} catch (MongoException e) {
			json = new JSONError(17);
		} catch (SQLException e) {
			json = new JSONError(15);
		}
		
		response.getWriter().println(json);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
