package tempApp;

import tendril.bean.Bean;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("TempName")
public class SingletonClass {

    @PostConstruct
    public void post1() {
        System.out.println("post1");
    }
    @PostConstruct
    protected void post2() {
        System.out.println("post2");
    }
    @PostConstruct
    void post3() {
        System.out.println("post3");
    }
	
	@Override
	public String toString() {
		return "TempClass Value = TBD";
	}
}
