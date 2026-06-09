package tempApp.lib1.test;

import org.junit.jupiter.api.Test;

import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;

public class Lib1LaunchTest {

	@Test
	public void testOriginalBeans() {
		ApplicationContextBuilder ctxBuilder = new ApplicationContextBuilder();
		ctxBuilder.setEnvironments("Lib1Test");
		ApplicationContext ctx = ctxBuilder.build();
		ctx.start();
	}
}
