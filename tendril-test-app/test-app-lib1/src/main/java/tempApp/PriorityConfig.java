package tempApp;

import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Configuration
public class PriorityConfig {

	public static final String PRIMARY1 = "Primary1";
	public static final String PRIMARY2 = "Primary2";
	public static final String BASIC1 = "Basic1";
	public static final String BASIC2 = "Basic2";
	public static final String BASIC3 = "Basic3";
	public static final String FALLBACK1 = "Fallback1";
	public static final String FALLBACK2 = "Fallback2";
	public static final String FALLBACK3 = "Fallback3";

	@Bean
	@Singleton
	@Primary
	@Option1
	StringWrapper primaryString1() {
		return new StringWrapper(PRIMARY1);
	}

	@Bean
	@Singleton
	@Primary
	@Option2
	StringWrapper primaryString2() {
		return new StringWrapper(PRIMARY2);
	}
	
	@Bean
	@Singleton
	@Option1
	@Option2
	StringWrapper basicString1() {
		return new StringWrapper(BASIC1);
	}
	
	@Bean
	@Singleton
	@Option1
	@Option2
	StringWrapper basicString2() {
		return new StringWrapper(BASIC2);
	}
	
	@Bean
	@Singleton
	@Option1
	@Option2
	StringWrapper basicString3() {
		return new StringWrapper(BASIC3);
	}
	
	@Bean
	@Singleton
	@Fallback
	@Option1
	@Named("Option1")
	StringWrapper fallbackString1() {
		return new StringWrapper(FALLBACK1);
	}
	
	@Bean
	@Singleton
	@Fallback
	@Option2
	@Named("Option2")
	StringWrapper fallbackString2() {
		return new StringWrapper(FALLBACK2);
	}
	
	@Bean
	@Singleton
	@Fallback
	@Message
	StringWrapper fallbackString3() {
		return new StringWrapper(FALLBACK3);
	}
	
}
