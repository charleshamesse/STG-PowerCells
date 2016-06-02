package beans;

import java.util.LinkedList;

import javax.script.ScriptEngine;

public class Variable {
	protected String shortName, name, expression, unit, group, topology;
	protected Double value;
	
	public Variable(String _name, String name, String expression, double value, String unit, String group) {
		this.shortName = _name;
		this.name = name;
		this.expression = expression;
		this.value = value;
		this.unit = unit;
		this.group = group;
	}
	
	public double eval(LinkedList<Variable> variables, ScriptEngine engine) {
		return this.value;
		// Sera override pour DynamicVariable
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getShortName() {
		return this.shortName;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public String getExpression() {
		return this.expression;
	}
	
	public String getGroup() {
		return this.group;
	}
	
	public String getUnit() {
		return this.unit;
	}
	
	public void setValue(double v) {
		this.value = v;
	}
	
	public String toString() {
		return "(" + this.name + ", " + this.value + ")";
	}
}
