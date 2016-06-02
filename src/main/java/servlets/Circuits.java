package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Circuit;
import dao.DaoFactory;
import dao.CircuitDao;
import com.google.gson.Gson;

/**
 * Servlet implementation class History
 */
@WebServlet("/Circuits")
public class Circuits extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private CircuitDao circuitDao;

    public void init() throws ServletException {
        DaoFactory daoFactory = DaoFactory.getInstance();
        this.circuitDao = daoFactory.getCircuitDao();
    }
	
	public Circuits() {
        super();
    }
	
	// List
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String json = new Gson().toJson(circuitDao.list());

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
	
	// Add
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Circuit circuit = getCircuitFromRequest(request);
		circuitDao.add(circuit);
		doGet(request, response);
	}
	
	// Update
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Circuit circuit = getCircuitFromRequest(request);
		circuitDao.update(circuit);
		doGet(request, response);
	}
	
	// Delete
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Circuit circuit = getCircuitFromRequest(request);
		int id = circuit.getId();
		circuitDao.delete(id);
		doGet(request, response);
	}
	
	private Circuit getCircuitFromRequest(HttpServletRequest request) throws IOException {
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    // Json treatment
		Gson gson = new Gson();
		Circuit circuit = gson.fromJson(data, Circuit.class);
		return circuit;
	}
}
