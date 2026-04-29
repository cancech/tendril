package tempApp;

import tempApp.lib1orig.Original;
import tempApp.lib1orig.OriginalNamed;
import tempApp.lib1orig.OriginalOption1;
import tendril.bean.Inject;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresEnv;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;

@Runner
@RequiresEnv("Lib1Test")
public class Lib1TestRunner implements TendrilRunner {

	// TODO add support for test injections and test annotations
	// TODO move this into test source folder

	@Inject
	Original originalBean;
	@Inject
	Original originalBean2;
	@Inject
	@Option1
	OriginalOption1 originalOption1Bean;
	@Inject
	@Option1
	OriginalOption1 originalOption1Bean2;
	@Inject
	@Named("originalNamed")
	OriginalNamed originalNamed;
	@Inject
	@Named("originalNamed")
	OriginalNamed originalNamed2;
	
	@Override
	public void run() {
		assertion(originalBean instanceof Original, "originalBean should be instance of Original");
		assertion(originalOption1Bean instanceof OriginalOption1, "originalBean should be instance of OriginalOption1");
		assertion(originalNamed instanceof OriginalNamed, "originalBean should be instance of OriginalNamed");

		assertion(originalBean.getInt() == 123, "originalBean should have a value of 123, instead it is " + originalBean.getInt());
		assertion(originalOption1Bean.getInt() == 234, "originalOption1Bean should have a value of 234, instead it is " + originalOption1Bean.getInt());
		assertion(originalNamed.getInt() == 345, "originalNamed should have a value of 345, instead it is " + originalNamed.getInt());

		assertion(originalBean == originalBean2, "originalBean is not a singleton");
		assertion(originalOption1Bean == originalOption1Bean2, "originalOption1Bean is not a singleton");
		assertion(originalNamed == originalNamed2, "originalNamed is not a singleton");
	}

	protected static void assertion(boolean value, String msg) {
		if (!value)
			throw new AssertionError(msg);
	}
}
