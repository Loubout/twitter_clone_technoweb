package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.MongoException;

import error.JSONError;
import service.UserServices;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreaterUserServlet
 */

public class CreateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		/*Recupération des parametres*/
		String login = request.getParameter("login");
		String pwd   = request.getParameter("pwd");
		String firstname  = request.getParameter("firstname");
		String lastname   =	request.getParameter("lastname");
		String photo =request.getParameter("photo");
		String desc =request.getParameter("desc");
		

		/*Instance de la réponse*/
		PrintWriter out = response.getWriter();

		/*On envoi au service*/
		JSONObject json = new JSONObject();

		try {
			json = UserServices.createUserService(login,pwd,firstname,lastname,photo,desc);
		} catch (InstantiationException e) {
			json = new JSONError(14);
		} catch (IllegalAccessException e) {
			json = new JSONError(16);
		} catch (MongoException e) {
			json = new JSONError(17);
		} catch (SQLException e) {
			json = new JSONError(15);
		} catch (JSONException e) {
			json = new JSONError(14);
			e.printStackTrace();
		}
	

		out.print(json.toString());



	}


}
