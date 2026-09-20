package tempApp;

import tendril.bean.Inject;
import tendril.bean.qualifier.Named;

public class DesiredParent {
	@Inject
	@Named("Option1")
	protected StringWrapper option1NamedStringWrapper;
}
