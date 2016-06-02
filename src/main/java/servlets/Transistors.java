package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Transistor;
import dao.DaoFactory;
import dao.TransistorDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class History
 */
@WebServlet("/Transistors")
public class Transistors extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private TransistorDao transistorDao;

    public void init() throws ServletException {
        DaoFactory daoFactory = DaoFactory.getInstance();
        this.transistorDao = daoFactory.getTransistorDao();
    }
	
	public Transistors() {
        super();
    }
	
	// List
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = new Gson().toJson(transistorDao.list());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
	
	// Add
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Transistor transistor = getTransistorFromRequest(request);
		transistorDao.add(transistor);
		doGet(request, response);
	}
	
	// Update
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Transistor transistor = getTransistorFromRequest(request);
		transistorDao.update(transistor);
		doGet(request, response);
	}
	
	// Delete
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Transistor transistor = getTransistorFromRequest(request);
		int id = transistor.getId();
		transistorDao.delete(id);
		doGet(request, response);
	}
	
	private Transistor getTransistorFromRequest(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    // Json treatment
		Gson gson = new Gson();
		Transistor transistor = gson.fromJson(data, Transistor.class);
		return transistor;
	}
}
