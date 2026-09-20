package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;

@Configuration
public class ArrayConfig {

	@Bean
	@Singleton
	ArrayContents[] buildArray() {
		return new ArrayContents[] {new ArrayContents(0), new ArrayContents(1), new ArrayContents(2), new ArrayContents(3), new ArrayContents(4), new ArrayContents(5)};
	}
}
