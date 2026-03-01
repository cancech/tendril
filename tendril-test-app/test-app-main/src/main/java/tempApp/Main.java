package tempApp;

import tendril.context.ApplicationContext;

public class Main {
	
	public static void main(String[] args) {
	    AbstractAppRunner.expectedMessage = "QWERTY";
	    AbstractAppRunner.expectedRunner = AppRunner1.class;
	    AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
	    
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "AppRunner1", "production");
        ctx.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
        ctx.addDynamicBlueprint(new DuplicationDetails("b", 234, 2.34));
        ctx.addDynamicBlueprint(new DuplicationDetails("c", 345, 3.45));
        ctx.start();
	}
	
}
