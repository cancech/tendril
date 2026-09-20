package tempApp;

import tendril.bean.Bean;
import tendril.bean.Singleton;

@Bean(DesiredInterface.class)
@Singleton
public class DesiredInterfaceImpl extends DesiredParent implements DesiredInterface {

	@Override
	public String getString() {
		return option1NamedStringWrapper.getString();
	}

}
