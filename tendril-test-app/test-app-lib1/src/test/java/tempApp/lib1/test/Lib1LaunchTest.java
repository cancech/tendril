package tempApp.lib1.test;

import org.junit.jupiter.api.Test;

import tendril.context.ApplicationContext;

public class Lib1LaunchTest {

	@Test
	public void testOriginalBeans() {
		ApplicationContext ctx = new ApplicationContext();
		ctx.setEnvironments("Lib1Test");
		ctx.start();
	}
}
