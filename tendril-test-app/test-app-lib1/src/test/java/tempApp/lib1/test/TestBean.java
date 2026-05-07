package tempApp.lib1.test;

import tendril.bean.Bean;
import tendril.bean.Singleton;

@Bean
@Singleton
public class TestBean {

	public int getValue() {
		return 123456789;
	}
	
	@Override
	public String toString() {
		return "TestBean blah blah blah";
	}
}
