package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;

@Configuration
public class IntWrapperConfig {

	@Bean(IntWrapper.class)
	@Singleton
	public IntWrapperImpl createWrapper1() {
		return new IntWrapperImpl(1);
	}

	@Bean(IntWrapper.class)
	@Singleton
	public IntWrapper createWrapper2() {
		return new IntWrapperImpl(2);
	}
}
