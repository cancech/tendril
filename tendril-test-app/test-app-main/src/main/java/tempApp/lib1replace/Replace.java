package tempApp.lib1replace;

import tempApp.lib1orig.Original;
import tendril.bean.Factory;
import tendril.bean.Replaces;
import tendril.bean.qualifier.Named;

@Replaces
@Factory
@Named("originalBean")
public class Replace extends Original {

	@Override
	public int getInt() {
		return -321;
	}
}
