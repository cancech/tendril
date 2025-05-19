package tempApp;

import tendril.context.ApplicationContext;

public class Main {
	
	public static void main(String[] args) {
	    AppRunner.expectedMessage = "QWERTY";
	    
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty");
        ctx.start();
	}
	
}
