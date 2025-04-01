package tempApp;

import tendril.bean.Bean;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.qualifier.Named;

@Bean
@Factory
public class FactoryClass {
    
    private static int numClasses = 0;
    private final int index;
    private final SingletonClass singleton;
    
    @Inject
    public FactoryClass(@Named("TempName") SingletonClass singleton) {
        this.singleton = singleton;
        index = numClasses++;
    }
    
    public FactoryClass(String name) {
        this((SingletonClass)null);
    }
    
    public FactoryClass(int value) {
        this((SingletonClass)null);
    }
	
	@Override
	public String toString() {
		return "FactoryClass index: " + index + " with singleton: " + singleton;
	}
}
