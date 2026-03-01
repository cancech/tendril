package tempApp;

import tendril.bean.duplicate.Blueprint;
import tendril.bean.duplicate.BlueprintDriver;

@Blueprint
public class DuplicationDetails implements BlueprintDriver {

	private final String name;
	private final int intValue;
	private final double dblValue;
	
	public DuplicationDetails(String name, int intValue, double dblValue) {
		this.name = name;
		this.intValue = intValue;
		this.dblValue = dblValue;
	}
	
	//@Override
	public String getName() {
		return name;
	}
	
	public int getInt() {
		return intValue;
	}
	
	public double getDouble() {
		return dblValue;
	}
}
