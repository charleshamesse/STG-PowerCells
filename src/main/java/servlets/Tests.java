package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Test;
import dao.DaoFactory;
import dao.TestDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class Tests
 */
@WebServlet("/Tests")
public class Tests extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private TestDao testDao;

    public void init() throws ServletException {
        DaoFactory daoFactory = DaoFactory.getInstance();
        this.testDao = daoFactory.getTestDao();
    }
	
	public Tests() {
        super();
    }
	
	// List
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = new Gson().toJson(testDao.list());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
	
	// Add
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Test test = getTestFromRequest(request);
		testDao.add(test);
		doGet(request, response);
	}
	
	// Delete
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Input
		Test test = getTestFromRequest(request);
		int id = test.getId();
		
		// Delete method
		testDao.delete(id);

		doGet(request, response);
	}
	
	private Test getTestFromRequest(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    // Json treatment
		Gson gson = new Gson();
		Test test = gson.fromJson(data, Test.class);
		return test;
	}
}
