package tempApp.lib1orig;

import tempApp.Option2;
import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("originalBean")
@Option2
public class Original {

	public int getInt() {
		return 123;
	}
}
