package beans;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Circuit {
	
	public JSONObject v;
	private int id;
	private String shortName, name, variables;
	private LinkedList<Variable> variableList;
	private JSONObject input;
	
	public Circuit(int id, String shortName, String name, String variables) {
		this.id = id;
		this.shortName = shortName;
		this.name = name;
		this.variables = variables;
	}
	
	
	// setInput : mettre les valeurs du formulaire de l'UI et instancier toutes les variables
	public void setInput(JSONObject input) {
		this.input = input;
		fillVariables();
	}
	
	// Circuit.fillVariables initialise toutes les variables presentes dans la simulation, en fonction de la topologie choisie.
	private void fillVariables() {
		LinkedList<Variable> _variables = new LinkedList<Variable>();
		JSONParser parser = new JSONParser();
		try {
			JSONArray JSONVariables = (JSONArray) parser.parse(variables);
			
			for (Object o : JSONVariables) {
				JSONObject var = (JSONObject) o;
				String name = (String) var.get("name");
				String value = (String) var.get("value");
				
				// Eviter les divisions par zero :
				if(value.equals("")) {
					value = "1";
				}
				
				System.out.println(name + ":" + value);
				if(var.get("type").equals("dynamic")) {
					_variables.add(new DynamicVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							(String) var.get("expression"),
							Double.parseDouble(value),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
				if(var.get("type").equals("form-dynamic")) {
					_variables.add(new DynamicVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							exp((String)var.get("_name")),
							Double.parseDouble(value),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
				if(var.get("type").equals("static")) {
					_variables.add(new StaticVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							(String) var.get("expression"),
							Double.parseDouble(value),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
				if(var.get("type").equals("form-static")) {
					_variables.add(new StaticVariable(
							(String) var.get("_name"),
							(String) var.get("name"),
							(String) var.get("expression"),
							form((String)var.get("_name")),
							(String) var.get("unit"), 
							(String) var.get("group")
					));
				}
			}
			this.variableList =  _variables;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public String getName() {
		return this.name;
	}
	
	// Retourne la LinkedList de variables
	public LinkedList<Variable> getVariables() {
		return variableList;
	}
	
	// Retourne la liste de variables en string
	public String getVariablesAsString() {
		return variables;
	}
	
	// Recupere la valeur du formulaire en double
	private double form(String s) {
		return Double.parseDouble((String) input.get(s));
	}
	
	private String exp(String s) {
		return (String) input.get(s);
	}
}
