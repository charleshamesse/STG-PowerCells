package servlets;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import beans.Transistor;
import beans.User;


/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
 
	private boolean connected;
	
    // Get status
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
    	User user = (User) session.getAttribute("user");
    	if(user != null) {
    		System.out.println(user.toString());
    	}
    	String json = new Gson().toJson(user.connected());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
	
	// Log in
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = getUserFromRequest(request);
		HttpSession session = request.getSession();
		session.setAttribute("user", user);
		
		doGet(request, response);
	}

    private static User getUserFromRequest(HttpServletRequest request) throws IOException {
    	StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();

		System.out.println(data);
	    // Json treatment
		Gson gson = new Gson();
		User user = gson.fromJson(data, User.class);
		return user;
    }
    

}