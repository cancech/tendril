package tempApp;

import tendril.bean.duplicate.Blueprint;

@Blueprint
public enum StaticDuplicate {

	COPY_1(1, 1.23, "First"),
	COPY_2(2, 2.34, "Second"),
	COPY_3(3, 3.45, "Third");
	
	private final int intValue;
	private final double dblValue;
	private final String strValue;
	
	private StaticDuplicate(int intValue, double dblValue, String strValue) {
		this.intValue = intValue;
		this.dblValue = dblValue;
		this.strValue = strValue;
	}
	
	public int getInteger() {
		return intValue;
	}
	
	public double getDouble() {
		return dblValue;
	}
	
	public String getString() {
		return strValue;
	}
}
