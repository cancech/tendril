package tempApp;

import tempApp.lib1dup.Lib1Blueprint;

public class DuplicationBlueprint extends Lib1Blueprint {

	private final int intValue;
	private final double dblValue;
	
	public DuplicationBlueprint(String name, int intValue, double dblValue) {
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
