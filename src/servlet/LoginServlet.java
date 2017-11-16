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
import service.UserServices;

/**
 * Servlet implementation class loginServlet
 */


public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	/* Renvoi des informations sur l'utilisateur et ses amis*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String login = request.getParameter("login");
		String pwd   = request.getParameter("pwd");

		JSONObject json = null;
		PrintWriter out = response.getWriter();

		try {
			json = UserServices.loginUserService(login, pwd);
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
