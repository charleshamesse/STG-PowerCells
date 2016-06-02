package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import beans.Simulation;
import com.google.gson.Gson;

public class Compute extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Aucune raison de demander http.get");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		String json;
		
		// Input
		StringBuffer sb = new StringBuffer();
	    try 
	    {
	      BufferedReader reader = request.getReader();
	      String line = null;
	      while ((line = reader.readLine()) != null)
	      {
	        sb.append(line);
	      }
	    } catch (Exception e) { e.printStackTrace(); }
	 
	    JSONParser parser = new JSONParser();
	    JSONObject sim = null;
	    try
	    {
	      sim = (JSONObject) parser.parse(sb.toString());
	    } catch (ParseException e) { e.printStackTrace(); }
	    
	    // On reprend le type du circuit
	    String circuit = (String) sim.get("circuit");
	    
	    // Simulation
	    Simulation s = new Simulation((JSONObject) sim.get("form"), circuit, Integer.parseInt((String)sim.get("max_iterations")));
	   
	    try {
			s.execute();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		// Output
	    resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		
		json = new Gson().toJson(s.out());
		
	    System.out.println(s.message());
	    
	    // La classe Simulation presente du Sequential Coupling qui est un AntiPattern 
		
		PrintWriter out = resp.getWriter();
		out.print(json);
    }
}
