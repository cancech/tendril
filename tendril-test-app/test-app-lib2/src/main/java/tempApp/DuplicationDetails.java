package tempApp;

import tempApp.lib1dup.Lib1Blueprint;
import tendril.bean.duplicate.Blueprint;

@Blueprint
public class DuplicationDetails extends Lib1Blueprint {

	private final int intValue;
	private final double dblValue;
	
	public DuplicationDetails(String name, int intValue, double dblValue) {
		super(name);
		this.intValue = intValue;
		this.dblValue = dblValue;
	}
	
	public int getInt() {
		return intValue;
	}
	
	public double getDouble() {
		return dblValue;
	}
}
