package tempApp.lib1dup;

import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Duplicate(Lib1Blueprint.class)
@Factory
public class ParentDuplicate {

	@Inject
	@Sibling
	Lib1Blueprint blueprint;
	
	public boolean isSameBlueprint(Lib1Blueprint other) {
		return blueprint == other;
	}
	
	public Lib1Blueprint getBlueprint() {
		return blueprint;
	}
	
}
