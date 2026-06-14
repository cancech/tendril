package tempApp;

import java.util.ArrayList;
import java.util.List;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.codegen.field.type.PrimitiveType;

@Configuration
public class TestConfig {

	@Bean
	@Singleton
	List<GenericWrapper<PrimitiveType>> getPrimitives() {
		List<GenericWrapper<PrimitiveType>> arr = new ArrayList<>();
		for(PrimitiveType t: PrimitiveType.values())
			arr.add(new GenericWrapper<>(t));
		return arr;
	}
}
