package tempApp;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("TempName")
public class SingletonClass {
	
	@Override
	public String toString() {
		return "TempClass Value = TBD";
	}
}
