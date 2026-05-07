package tempApp.lib1.test;

import tempApp.Option1;
import tempApp.StringWrapper;
import tendril.bean.Replaces;
import tendril.bean.Singleton;

@Replaces
@Singleton
@Option1
public class ReplaceStringWrapper extends StringWrapper {

	public ReplaceStringWrapper() {
		super("ReplacedWrapper");
	}

}
