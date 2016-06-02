package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Diode;
import dao.DaoFactory;
import dao.DiodeDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class Diodes
 */
@WebServlet("/Diodes")
public class Diodes extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private DiodeDao diodeDao;

    public void init() throws ServletException {
        DaoFactory daoFactory = DaoFactory.getInstance();
        this.diodeDao = daoFactory.getDiodeDao();
    }
	
	public Diodes() {
        super();
    }
	
	// List
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = new Gson().toJson(diodeDao.list());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
	
	// Add
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Diode diode = getDiodeFromRequest(request);
		diodeDao.add(diode);
		doGet(request, response);
	}
	
	// Update
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Diode diode = getDiodeFromRequest(request);
		diodeDao.update(diode);
		doGet(request, response);
	}
	
	// Delete
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Diode diode = getDiodeFromRequest(request);
		int id = diode.getId();
		diodeDao.delete(id);
		doGet(request, response);
	}
	
	private Diode getDiodeFromRequest(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    // Json treatment
		Gson gson = new Gson();
		Diode diode = gson.fromJson(data, Diode.class);
		System.out.println(data);
		return diode;
	}
}
