package tempApp;

import java.util.Arrays;
import java.util.List;

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
	
	@Bean
	@Singleton
	List<Integer> createIntList() {
		return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
	}
	
	@Bean
	@Singleton
	List<String> createStringList() {
		return Arrays.asList("a", "b", "c", "d");
	}
}
