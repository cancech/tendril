package tempApp;

import tendril.bean.Bean;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean(IntWrapper.class)
@Singleton
@Named("standaloneIntWrapper")
public class IntWrapperImpl implements IntWrapper {

	private final int value;
	
	@Inject
	public IntWrapperImpl() {
		this(12345);
	}
	
	public IntWrapperImpl(int value) {
		this.value = value;
	}
	
	@Override
	public int getInt() {
		return value;
	}

}
