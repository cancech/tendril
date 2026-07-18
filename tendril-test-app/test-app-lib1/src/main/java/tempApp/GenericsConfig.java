package tempApp;

import java.util.Arrays;
import java.util.List;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Configuration
public class GenericsConfig {
	
	@Bean
	@Singleton
	List<Integer> createIntList() {
		return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
	}
	
	@Bean
	@Singleton
    @Named("stringList")
	List<String> createStringList() {
		return Arrays.asList("a", "b", "c", "d");
	}
}
