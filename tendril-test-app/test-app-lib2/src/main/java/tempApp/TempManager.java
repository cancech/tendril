package tempApp;

import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Configuration
public class TempManager {
    
    private static int numInstances = 0;
    private static int numPostConstruct = 0;
    private static int numVal1Instances = 0;
    private static int numVal2Instances = 0;
    
    public static void assertSingleton() {
        assert(numInstances == 1);
        assert(numPostConstruct == 1);
        assert(numVal1Instances == 1);
        assert(numVal2Instances >= 1);
    }
    
    public static void reset() {
        numInstances = 0;
        numPostConstruct = 0;
        numVal1Instances = 0;
        numVal2Instances = 0;
    }
    
    public String other;

	private final FactoryClass cls;
	
	// TODO Check for cycles
//	@Inject
//	@MyTypeId(MyType.VAL2)
//	Object obj;
	
	@Inject
	public TempManager(FactoryClass cls) {
	    numInstances++;
		this.cls = cls;
	}
	
	@PostConstruct
	void here() {
	    System.err.println("I AM HERE AND I AM CALLED");
	    numPostConstruct++;
	}
	
	@Bean
	@Singleton
	@Named("TempString")
	@MyTypeId(MyType.VAL1)
	public String doSomething(SingletonClass sgClass, FactoryClass fClass, @MyTypeId(MyType.VAL2) Object something) {
	    numVal1Instances++;
		return "Singleton = " + sgClass.toString() + ", Factory Field = " + cls.toString() + ", Factory Param = " + fClass.toString() + " something: " + something;
	}
	
	@Bean
	@Factory
	@MyTypeId(MyType.VAL2)
	public Object buildSomething() {
	    numVal2Instances++;
	    return new Object();
	}
}
