package tendril.junit5.beans;

import tendril.bean.duplicate.BasicBlueprint;
import tendril.bean.duplicate.Blueprint;

/**
 * {@link Blueprint} to use for testing
 */
public class TestBlueprint extends BasicBlueprint {

	/**
	 * CTOR
	 * 
	 * @param name {@link String} to apply to the duplicate
	 */
	public TestBlueprint(String name) {
		super(name);
	}

}
