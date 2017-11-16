package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import service.UserServices;
import error.JSONError;

public class AddFriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idFriend = request.getParameter("loginFriend");
		String key = request.getParameter("key");
		
		JSONObject json = null;
		PrintWriter out = response.getWriter();

		try {
			json = UserServices.addFriendService(idFriend,key);
		} 	
		catch (SQLException e2){
			json = new JSONError(15);
		}
		catch(Exception e4){
			json = new JSONError(404);
		}
		
		out.print(json.toString());
		
	}

}
