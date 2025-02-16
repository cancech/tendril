package tempApp;

import tendril.bean.Bean;
import tendril.bean.Factory;

@Bean
@Factory
public class FactoryClass {
    
    private static int numClasses = 0;
    private final int index;
    
    public FactoryClass() {
        index = numClasses++;
    }
	
	@Override
	public String toString() {
		return "FactoryClass index: " + index;
	}
}
