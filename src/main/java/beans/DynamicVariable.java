package beans;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class DynamicVariable extends Variable {

	public DynamicVariable(String _name, String name, String expression, double value, String unit, String group) {
		super(_name, name, expression, value, unit, group);
		this.expression = expression;
		this.name = name;
		this.value = value;
		this.shortName = _name;
	}
	
	public double eval(LinkedList<Variable> variables, ScriptEngine engine) {
		
	    // On met a jour le ScriptEngine avec les dernieres valeurs a chaque fois
	    for(int i=0; i<variables.size(); i++) {
	    	engine.put(variables.get(i).getShortName(), variables.get(i).getValue());
	    }
    	
	    Object result;
		try {
			String finalExpression = expression.replace("exp", "Math.exp")
					.replace("pow", "Math.pow")
					.replace("sqrt", "Math.sqrt")
					.replace("log", "Math.log")
					.replace("abs", "Math.abs")
					.replace("atan", "Math.atan")
					.replace("PI", "Math.PI");
			result = engine.eval(finalExpression);
			if(Double.isInfinite(Double.parseDouble(String.valueOf(result)))) { result = value; } 
		} catch (ScriptException e) {
			e.printStackTrace();
			result = "Erreur dans DynamicVariable.eval(). Impossible de calculer " + name + " (" + shortName + ").";
		}
		return (Double) result;
	}
}