package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

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

	@Bean(IntWrapper.class)
	@Singleton
	@Named("configIntWrapper")
	public IntWrapperImpl createWrapper3() {
		return new IntWrapperImpl(3);
	}
}
