package tempApp.lib1replace;

import tempApp.Option1;
import tempApp.lib1orig.OriginalOption1;
import tendril.bean.Factory;
import tendril.bean.Replaces;

@Replaces(OriginalOption1.class)
@Factory
@Option1
public class ReplaceOption1 extends OriginalOption1 {

	@Override
	public int getInt() {
		return -432;
	}
}
