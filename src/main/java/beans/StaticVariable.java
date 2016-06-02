package beans;

import java.util.LinkedList;
import java.util.List;

public class StaticVariable extends Variable {

	
	public StaticVariable(String _name, String name, String expression, double value, String unit, String group) {
		super(_name, name, expression, value, unit, group);
	}
	
	// StaticVariable.eval() n'est pas implementee, elle reprend simplement celle de la classe parent Variable.eval() qui retourne simplementvalue.
	// Cette classe pourra sans doute etre supprimee a terme
}