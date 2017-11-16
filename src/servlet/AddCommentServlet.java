package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.MongoException;

import error.JSONError;
import service.TweetServices;

/**
 * Servlet implementation class CommentServlet
 */

public class AddCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/*Recupération des parametres*/
		String key = request.getParameter("key");
		String txt = request.getParameter("txt");
		String color = request.getParameter("color");

		/*Instance de la réponse*/
		PrintWriter out = response.getWriter();

		/*On envoi au service*/
		JSONObject json = new JSONObject();

		/*On lance */

		try {
			json = TweetServices.addCommentService(key,txt,color);
		} catch (InstantiationException e) {
			json = new JSONError(14);
		} catch (IllegalAccessException e) {
			json = new JSONError(16);
		} catch (MongoException e) {
			json = new JSONError(17);
		} catch (SQLException e) {
			json = new JSONError(15);
		}

		/*On renvoi*/
		out.print(json.toString());

	}


}
