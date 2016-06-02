package beans;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.simple.JSONObject;

import com.google.gson.Gson;

import dao.CircuitDao;
import dao.DaoFactory;

public class Simulation {
	private Circuit circuit;
	private LinkedList<Variable> variables;
	private long duration;
	private int MAX_ITERATIONS;
	private int NUMBER_VARIABLES;
	private boolean success;
	private String message;
	
	public Simulation(JSONObject input, String circuitId, int m_i) {
		circuit = getCircuit(Integer.parseInt(circuitId)); // a changer
		circuit.setInput(input);
		this.variables = circuit.getVariables();
		this.MAX_ITERATIONS = m_i;
		this.NUMBER_VARIABLES = variables.size();
		this.success = true;
	}
	
	private Circuit getCircuit(int id) {
        DaoFactory daoFactory = DaoFactory.getInstance();
        CircuitDao circuitDao = daoFactory.getCircuitDao();
        return circuitDao.get(id);
	}
	
	public LinkedList<Variable> execute() throws MalformedURLException, ScriptException, IOException {
		
	    // On lance ScriptEngine
		ScriptEngineManager mgr = new ScriptEngineManager();
	    ScriptEngine engine = mgr.getEngineByName("js");
	    
		// Calcul iteratif de toutes les valeurs. 
		// Les valeurs de type StaticVariable sont juste laissees telles quelles, celles de type DynamicVariable, en revanche, sont reevaluees a chaque tour
		// On calcule aussi le temps d'execution avec nanoTime();
		message = "";
		long startTime = System.nanoTime();
		outerloop:
		for(int i=0; i<MAX_ITERATIONS; i++) {
			System.out.println("-Iteration #" + (i+1));
			for(int j=0; j<variables.size(); j++) {
				System.out.println("--Sous-iteration #" + (j+1) + ", " + variables.get(j).getName() + ": " + variables.get(j).getValue());
				try {
					variables.get(j).setValue(variables.get(j).eval(this.variables, engine));
				}
				
				catch(Exception e) {
					success = false;
					message = message + "Itération #" + String.valueOf(i + 1) + "-" + String.valueOf(j + 1) + " \t Impossible de calculer la variable " + variables.get(j).getName() + " (" + variables.get(j).getShortName()  + ") \n";
					break outerloop;
				}
			}
		}
		long endTime = System.nanoTime();
		this.duration = endTime - startTime;

		return variables;
	}
	
	public LinkedList<Variable> getVariables() {
		return variables;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public boolean succeeded() {
		return success;
	}
	
	public String message() {
		return message;
	}
	
	public int getNumberVariables() {
		return NUMBER_VARIABLES;
	}
	
	public List<Variable> out() {
		List<Variable> outputList = new LinkedList<Variable>();
		
		for(int j=0; j<variables.size(); j++) {
			outputList.add(variables.get(j));		
			if(Double.isInfinite(variables.get(j).getValue()) || Double.isNaN(variables.get(j).getValue())) {
				String nameString = variables.get(j).getName() + " (" + variables.get(j).getShortName() + ")";
				message += (Double.isInfinite(variables.get(j).getValue())) ? "Valeur infinie pour " + nameString + "\n" :  "Valeur incorrecte pour " + nameString + "\n";
				variables.get(j).setValue(0.0);
				success = false;
			}		
		}
		
		if(success) {
			message = String.valueOf(this.duration/1000000);
		}
		
		// On ajoute deux variables de statut pour gerer l'affichage des erreurs
		outputList.add(new Variable("success", "Simulation sans faute", String.valueOf(success), 0.0, "", "Hidden"));
		outputList.add(new Variable("message", "Message", message, 0.0, "", "Hidden"));
		
		return outputList;
	}
}
