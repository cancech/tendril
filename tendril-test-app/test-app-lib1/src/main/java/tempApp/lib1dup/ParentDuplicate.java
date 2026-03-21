package tempApp.lib1dup;

import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.duplicate.Sibling;

@Lib1BlueprintBlueprint
@Factory
public class ParentDuplicate {

	@Inject
	@Sibling
	Lib1Blueprint blueprint;
	
	public boolean isSameBlueprint(Lib1Blueprint other) {
		return blueprint == other;
	}
	
}
