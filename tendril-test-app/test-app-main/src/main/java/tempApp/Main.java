package tempApp;

import tendril.bean.qualifier.Descriptor;
import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;

public class Main {
	
	public static void main(String[] args) {
	    AbstractAppRunner.expectedMessage = "QWERTY";
	    AbstractAppRunner.expectedEnvironment = "production";
	    AbstractAppRunner.expectedRunner = AppRunner1.class;
	    AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
	    AbstractAppRunner.expectedDblValue = 123;
	    AbstractAppRunner.expectedManualBean = 135;
	    
	    System.setProperty("testProperty", "");
        ApplicationContextBuilder ctxBuilder = new ApplicationContextBuilder();
        ctxBuilder.setEnvironments("uppercase", "qwerty", "AppRunner1", "production");
        ctxBuilder.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
        ctxBuilder.addDynamicBlueprint(new DuplicationDetails("b", 234, 2.34));
        ctxBuilder.addDynamicBlueprint(new DuplicationDetails("c", 345, 3.45));
        ctxBuilder.addDynamicBlueprint(new ClassDuplicate("c1"));
        ctxBuilder.addDynamicBlueprint(new ClassDuplicate("c2"));
        
        ApplicationContext ctx = ctxBuilder.build();
        ctx.registerBean(new ManualBean(135), new Descriptor<>(ManualBean.class));
        ctx.start();
	}
}
