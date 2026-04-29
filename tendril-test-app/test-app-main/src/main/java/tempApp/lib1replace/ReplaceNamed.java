package tempApp.lib1replace;

import tempApp.lib1orig.OriginalNamed;
import tendril.bean.Factory;
import tendril.bean.Replaces;
import tendril.bean.qualifier.Named;

@Replaces(Object.class)
@Factory
@Named("originalNamed")
public class ReplaceNamed extends OriginalNamed {

	@Override
	public int getInt() {
		return -543;
	}
}
