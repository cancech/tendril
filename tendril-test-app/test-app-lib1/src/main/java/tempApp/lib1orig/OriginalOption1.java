package tempApp.lib1orig;

import tempApp.Option1;
import tendril.bean.Bean;
import tendril.bean.Singleton;

@Option1
@Bean
@Singleton
public class OriginalOption1 {

	public int getInt() {
		return 234;
	}
}
