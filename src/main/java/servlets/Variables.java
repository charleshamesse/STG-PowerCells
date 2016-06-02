package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import beans.Circuit;
import beans.DynamicVariable;
import beans.StaticVariable;
import beans.Variable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dao.CircuitDao;
import dao.DaoFactory;
import dao.TransistorDao;


/**
 * Servlet implementation class Variables
 */
@WebServlet("/Variables")
public class Variables extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private LinkedList<Variable> variables;
	Circuit circuit;
    private CircuitDao circuitDao;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// Input
		StringBuilder buffer = new StringBuilder();
	    BufferedReader reader = req.getReader();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        buffer.append(line);
	    }
	    String data = buffer.toString();
	 
	    JSONParser inputParser = new JSONParser();
	    JSONObject input = null;
	    try
	    {
	      input = (JSONObject) inputParser.parse(data);
	    } catch (ParseException e) { e.printStackTrace(); }
	    
	    
	    String circuitId = (String) input.get("circuit");

	    System.out.println(circuitId);
	    

	    DaoFactory daoFactory = DaoFactory.getInstance();
	    this.circuitDao = daoFactory.getCircuitDao();
	    
	    // Variables - similaire au script de la classe Circuit.java
	 	LinkedList<Variable> _variables = new LinkedList<Variable>();
	 	JSONParser parser = new JSONParser();
	 	try {
	 		JSONArray JSONVariables = (JSONArray) parser.parse(this.circuitDao.get(Integer.parseInt(circuitId)).getVariablesAsString());
	 		for (Object o : JSONVariables) {
	 			JSONObject var = (JSONObject) o;
				String value = (String) var.get("value");
	 			// Eviter les divisions par zero :
	 			if(value.equals("")) {
	 				value = "1";
				}
 				if(var.get("type").equals("form-dynamic")) {
	 				_variables.add(new DynamicVariable(
	 						(String) var.get("_name"),
	 						(String) var.get("name"),
	 						(String) var.get("expression"),
	 						(Double) Double.valueOf(value),
							(String) var.get("unit"), 
 							(String) var.get("group")
					));
	 			}
	 			if(var.get("type").equals("form-static")) {
	 				_variables.add(new StaticVariable(
	 						(String) var.get("_name"),
	 						(String) var.get("name"),
	 						(String) var.get("expression"),
	 						(Double) Double.valueOf(value),
	 						(String) var.get("unit"), 
	 						(String) var.get("group")
	 				));
	 			}
	 		}
			this.variables =  _variables;
	 	} catch (Exception e) {
	 		e.printStackTrace();
	 	}
	    
	    /*
	      
	    String circuitName = (String) input.get("circuit");

	    System.out.println(circuitName);
	    
	    // Variables - similaire au script de la classe Circuit.java
		LinkedList<Variable> _variables = new LinkedList<Variable>();
		JSONParser parser = new JSONParser();
		try {
			JSONArray JSONVariables = (JSONArray) parser.parse(new FileReader(getServletContext().getRealPath("/") + "circuits/" + circuitName + ".json"));
			for (Object o : JSONVariables) {
				JSONObject var = (JSONObject) o;
				String value = (String) var.get("value");
				// Eviter les divisions par zero :
				if(value.equals("")) {
					value = "1";
				}
				if(var.get("type").equals("form-dynamic")) {
					_variables.add(new DynamicVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							(String) var.get("expression"),
							(Double) Double.valueOf(value),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
				if(var.get("type").equals("form-static")) {
					_variables.add(new StaticVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							(String) var.get("expression"),
							(Double) Double.valueOf(value),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
			}
			this.variables =  _variables;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		*/
		
		// Output	

		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		Gson gson = new Gson();
		Type listType = new TypeToken<LinkedList<Variable>>() {}.getType();
		String json = gson.toJson(this.variables, listType);
		PrintWriter out = resp.getWriter();
		out.print(json);
    }
}
