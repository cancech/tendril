package tempApp;

import java.io.FileNotFoundException;

import tendril.bean.Bean;
import tendril.bean.Singleton;

/**
 * Test bean whose CTOR includes a throws exception for testing purposes
 */
@Bean
@Singleton
public class ExceptionCtorBean {
	
	public ExceptionCtorBean() throws FileNotFoundException {
		
	}

	public boolean isCreated() {
		return true;
	}
}
