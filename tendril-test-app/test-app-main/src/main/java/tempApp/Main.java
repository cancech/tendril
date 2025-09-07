package tempApp;

import tendril.context.ApplicationContext;

public class Main {
	
	public static void main(String[] args) {
	    AbstractAppRunner.expectedMessage = "QWERTY";
	    AbstractAppRunner.expectedRunner = AppRunner1.class;
	    AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
	    
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "AppRunner1", "production");
        ctx.start();
	}
	
}
