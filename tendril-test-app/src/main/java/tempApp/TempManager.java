package tempApp;

import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Configuration
public class TempManager {
    
    public String other;

	private final FactoryClass cls;
	
	@Inject
	public TempManager(FactoryClass cls) {
		this.cls = cls;
	}
	
	@Bean
	@Singleton
	@Named("TempString")
	@MyTypeId(MyType.VAL1)
	public String doSomething(SingletonClass sgClass, FactoryClass fClass) {
		return "Singleton = " + sgClass.toString() + ", Factory Field = " + cls.toString() + ", Factory Param = " + fClass.toString();
	}
}
