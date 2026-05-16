package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Replaces;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresEnv;

@Configuration
public class ReplacesConfig {

	@Bean
	@Singleton
	@Named("testReplacementData")
	Lib2DataStruct origData() {
		return new Lib2DataStruct("orig");
	}
	
	@Replaces
	@Singleton
	@RequiresEnv("production")
	@Named("testReplacementData")
	Lib2DataStruct prodData() {
		return new Lib2DataStruct("production");
	}
	
	@Replaces
	@Singleton
	@RequiresEnv("test")
	@Named("testReplacementData")
	Lib2DataStruct testData() {
		return new Lib2DataStruct("test");
	}
}
