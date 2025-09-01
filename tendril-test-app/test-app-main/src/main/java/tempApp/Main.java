package tempApp;

import tendril.context.ApplicationContext;

public class Main {
	
	public static void main(String[] args) {
	    AppRunner1.expectedMessage = "QWERTY";
	    AppRunner1.expectedRunner = AppRunner1.class;
	    
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "AppRunner1");
        ctx.start();
	}
	
}
