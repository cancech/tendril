package tempApp;

import tendril.bean.Bean;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.qualifier.Named;

@Bean
@Factory
public class FactoryClass {

    private static int numClasses = 0;
    private static int numPostConstruct = 0;
    
    public static void assertFactory() {
        assert(numClasses > 1);
        assert(numPostConstruct == numClasses);
    }
    
    public static void reset() {
        numClasses = 0;
        numPostConstruct = 0;
    }
    
    private final int index;
    private final SingletonClass singleton;
    
    @Inject
    public FactoryClass(@Named("TempName") SingletonClass singleton) {
        this.singleton = singleton;
        index = numClasses++;
    }
    
    @Inject
    private FactoryClass(@Named("TempName") SingletonClass singleton, @Named("TempName") SingletonClass singleton2) {
        this(singleton);
        // This should NOT be called
        assert(false);
    }
    
    public FactoryClass(String name) {
        this((SingletonClass)null);
    }
    
    public FactoryClass(int value) {
        this((SingletonClass)null);
    }
    
    @PostConstruct
    void postConstruct() {
        numPostConstruct++;
    }
	
	@Override
	public String toString() {
		return "FactoryClass index: " + index + " with singleton: " + singleton;
	}
}
