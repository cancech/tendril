package tempApp;

import tempApp.base.AbstractAppRunner;
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
        ctxBuilder.addBlueprint(new DuplicationBlueprint("a", 123, 1.23));
        ctxBuilder.addBlueprint(new DuplicationBlueprint("b", 234, 2.34));
        ctxBuilder.addBlueprint(new DuplicationBlueprint("c", 345, 3.45));
        ctxBuilder.addBlueprint(new ClassBlueprint("c1"));
        ctxBuilder.addBlueprint(new ClassBlueprint("c2"));
        for (StaticBlueprint sd: StaticBlueprint.values())
        	ctxBuilder.addBlueprint(sd);
        for (EnumBlueprint ed: EnumBlueprint.values())
        	ctxBuilder.addBlueprint(ed);
        
        ApplicationContext ctx = ctxBuilder.build();
        ctx.registerBean(new ManualBean(135), new Descriptor<>(ManualBean.class));
        ctx.start();
	}
}
