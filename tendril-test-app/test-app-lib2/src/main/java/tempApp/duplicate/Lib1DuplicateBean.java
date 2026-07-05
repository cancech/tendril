package tempApp.duplicate;

import tempApp.lib1dup.Lib1Blueprint;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Duplicate(Lib1Blueprint.class)
@Singleton
public class Lib1DuplicateBean {

	private final String name;
	
	@Inject
	public Lib1DuplicateBean(@Sibling Lib1Blueprint blueprint) {
		this.name = blueprint.getName();
	}
	
	public String getName() {
		return name;
	}
}
