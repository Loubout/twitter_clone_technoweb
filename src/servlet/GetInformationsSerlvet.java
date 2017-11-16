package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import service.UserServices;
import error.JSONError;


/* Renvoi les informations concernant un ou plusieurs profil a partir de l'id */
public class GetInformationsSerlvet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String ids = request.getParameter("ids");
		
		JSONObject json = null;
		PrintWriter out = response.getWriter();

		try {
			json = UserServices.getInformations(ids);
		} 	
		catch(Exception e4){
			json = new JSONError(404);
		}
		
		out.print(json.toString());
		
	}

}
