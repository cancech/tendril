package tempApp;

import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Duplicate(StaticBlueprint.class)
@Singleton
public class Lib2StaticBean {

	@Inject
	@Sibling
	StaticBlueprint blueprint; 
}
