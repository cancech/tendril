package tempApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tendril.bean.AutoCreate;
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
	
	@Bean
	@Singleton
	ArrayList<Double> createDoubleList() {
		return new ArrayList<Double>(Arrays.asList(1.2, 2.3, 3.4, 4.5));
	}
	
	@Bean
	@Singleton
	@Named("This configuration bean is not used")
	BeanNotReferenced notUsedAnywhere() {
		return new BeanNotReferenced();
	}
	
	@Bean
	@Singleton
	@AutoCreate
	@Named("This configuration bean is not used but is auto-created")
	BeanNotReferenced notUsedAnywhereAutoCreate() {
		return new BeanNotReferenced();
	}
}
