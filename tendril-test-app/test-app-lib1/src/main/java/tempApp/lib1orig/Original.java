package tempApp.lib1orig;

import tendril.bean.Bean;
import tendril.bean.Singleton;

@Bean
@Singleton
public class Original {

	public int getInt() {
		return 123;
	}
}
