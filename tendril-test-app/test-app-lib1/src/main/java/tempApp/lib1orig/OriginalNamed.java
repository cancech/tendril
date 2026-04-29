package tempApp.lib1orig;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Named("originalNamed")
@Bean
@Singleton
public class OriginalNamed {

	public int getInt() {
		return 345;
	}
}
