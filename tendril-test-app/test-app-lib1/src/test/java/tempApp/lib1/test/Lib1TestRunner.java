package tempApp.lib1.test;

import org.junit.jupiter.api.Assertions;

import tempApp.Option1;
import tempApp.Option2;
import tempApp.PriorityConfig;
import tempApp.StringWrapper;
import tempApp.lib1orig.Original;
import tempApp.lib1orig.OriginalNamed;
import tempApp.lib1orig.OriginalOption1;
import tendril.bean.Inject;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresEnv;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;
import tendril.test.assertions.ClassAssert;

@Runner
@RequiresEnv("Lib1Test")
public class Lib1TestRunner implements TendrilRunner {

	// TODO add support for test annotations

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
	@Inject
	TestBean testBean;
	@Inject
	@Option1
	StringWrapper opt1Wrapper;
	@Inject
	@Option2
	StringWrapper opt2Wrapper;
	
	@Override
	public void run() {
		ClassAssert.assertInstance(Original.class, originalBean);
		ClassAssert.assertInstance(OriginalOption1.class, originalOption1Bean);
		ClassAssert.assertInstance(OriginalNamed.class, originalNamed);

		Assertions.assertEquals(123, originalBean.getInt());
		Assertions.assertEquals(234, originalOption1Bean.getInt());
		Assertions.assertEquals(345, originalNamed.getInt());
		

		Assertions.assertEquals(originalBean, originalBean2);
		Assertions.assertEquals(originalOption1Bean, originalOption1Bean2);
		Assertions.assertEquals(originalNamed, originalNamed2);
		
		Assertions.assertEquals(123456789, testBean.getValue());

		ClassAssert.assertInstance(ReplaceStringWrapper.class, opt1Wrapper);
		ClassAssert.assertInstance(StringWrapper.class, opt2Wrapper);
		ClassAssert.assertNotInstance(ReplaceStringWrapper.class, opt2Wrapper);
		Assertions.assertEquals("ReplacedWrapper", opt1Wrapper.getString());
		Assertions.assertEquals(PriorityConfig.PRIMARY2, opt2Wrapper.getString());
	}
}
