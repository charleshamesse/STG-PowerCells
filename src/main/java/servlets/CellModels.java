package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Jama.Matrix;

import beans.Test;
import beans.CellModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class CellModels
 */
@WebServlet("/CellModels")
public class CellModels extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    
	public CellModels() {
        super();
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Input
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	    
	    Gson gson = new Gson();
	    Type type = new TypeToken<Collection<Test>>(){}.getType();
	    List<Test> tests = gson.fromJson(data, type);
	    
		// Model
	    CellModel model = new CellModel(tests);
	    model.build();
	    double[] coefficients = model.getCoefficients();
		
	    // Output
		String json = new Gson().toJson(coefficients);

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		PrintWriter out = response.getWriter();
		out.print(json);
	}
}
