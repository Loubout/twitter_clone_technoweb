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
import service.UserServices;

public class RemoveFriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idFriend = request.getParameter("idFriend");
		String key = request.getParameter("key");
		
		JSONObject json = null;
		PrintWriter out = response.getWriter();

		try {
			json = UserServices.removeFriendService(idFriend,key);
		} catch (InstantiationException e) {
			json = new JSONError(14);
		} catch (IllegalAccessException e) {
			json = new JSONError(16);
		} catch (MongoException e) {
			json = new JSONError(17);
		} catch (SQLException e) {
			json = new JSONError(15);
		}
		out.print(json.toString());
			
	}

}
