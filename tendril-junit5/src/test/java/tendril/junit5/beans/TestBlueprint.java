package tendril.junit5.beans;

import tendril.bean.duplicate.BasicBlueprintDriver;
import tendril.bean.duplicate.Blueprint;
import tendril.bean.duplicate.BlueprintDriver;

/**
 * {@link BlueprintDriver} to use for testing
 */
@Blueprint
public class TestBlueprint extends BasicBlueprintDriver {

	/**
	 * CTOR
	 * 
	 * @param name {@link String} to apply to the duplicate
	 */
	public TestBlueprint(String name) {
		super(name);
	}

}
