package tempApp;

import tendril.bean.Configuration;
import tendril.bean.Replaces;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Configuration
public class ReplaceConfig {

	@Replaces(IntWrapper.class)
	@Singleton
	@Named("configIntWrapper")
	ReplaceIntWrapper replaceWrapper() {
		return new ReplaceIntWrapper(-321);
	}
}
