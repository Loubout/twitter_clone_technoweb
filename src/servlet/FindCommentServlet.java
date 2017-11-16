package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;

import error.JSONError;
import service.TweetServices;

/**
 * Servlet implementation class CommentServlet
 */

public class FindCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*Recupération des parametres*/
		 String friends = request.getParameter("friends");
		 String key     = request.getParameter("key");
		 String query   = request.getParameter("query");


		/*Instance de la réponse*/
		PrintWriter out = response.getWriter();

		/*On envoi au service*/
		JSONObject json = new JSONObject();
		try {
			json = TweetServices.searchComments(key, query, friends);
		} catch (MongoException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*On lance */


		/*On renvoie*/
		out.print(json.toString());

	}


}
