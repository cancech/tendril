package tempApp;

import tendril.bean.Inject;
import tendril.bean.Replaces;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Replaces(IntWrapper.class)
@Singleton
@Named("standaloneIntWrapper")
public class ReplaceIntWrapper implements IntWrapper {

	private final int value;
	
	@Inject
	public ReplaceIntWrapper() {
		this(-123);
	}
	
	public ReplaceIntWrapper(int value) {
		this.value = value;
	}
	
	@Override
	public int getInt() {
		return value;
	}
}
