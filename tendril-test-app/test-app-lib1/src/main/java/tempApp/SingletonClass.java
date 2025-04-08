package tempApp;

import tendril.bean.Bean;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("TempName")
public class SingletonClass {
    
    private static int numInstances = 0;
    private static int numTimesPost1Called = 0;
    private static int numTimesPost2Called = 0;
    private static int numTimesPost3Called = 0;
    
    public static void assertSingleton() {
        assert(numInstances == 1);
        assert(numTimesPost1Called == 1);
        assert(numTimesPost2Called == 1);
        assert(numTimesPost3Called == 1);
    }
    
    public static void reset() {
        numInstances = 0;
        numTimesPost1Called = 0;
        numTimesPost2Called = 0;
        numTimesPost3Called = 0;
    }
    
    public SingletonClass() {
        numInstances++;
    }

    @PostConstruct
    public void post1() {
        numTimesPost1Called++;
        System.out.println("post1");
    }
    @PostConstruct
    protected void post2() {
        numTimesPost2Called++;
        System.out.println("post2");
    }
    @PostConstruct
    void post3() {
        numTimesPost3Called++;
        System.out.println("post3");
    }
	
	@Override
	public String toString() {
		return "TempClass Value = TBD";
	}
}
