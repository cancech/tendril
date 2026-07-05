/*
 * Copyright 2025 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tempApp.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tempApp.AppRunner1;
import tempApp.AppRunner2;
import tempApp.ClassBlueprint;
import tempApp.DuplicationBlueprint;
import tempApp.EnumBlueprint;
import tempApp.FactoryClass;
import tempApp.ManualBean;
import tempApp.MultiEnvBean1;
import tempApp.MultiEnvBean2;
import tempApp.RunnableConfig;
import tempApp.SingletonClass;
import tempApp.StaticBlueprint;
import tempApp.TempManager;
import tempApp.base.AbstractAppRunner;
import tendril.TendrilStartupException;
import tendril.bean.qualifier.Descriptor;
import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;

/**
 * Test which launches the application, allowing for it to be used as part of a unit test suite
 */
public class LaunchTest {
    
    @AfterEach
    public void reset() {
        SingletonClass.reset();
        FactoryClass.reset();
        TempManager.reset();
        AbstractAppRunner.reset();
        RunnableConfig.reset();
    }
    
    private void addEnumBlueprints(ApplicationContextBuilder builder) {
        for (StaticBlueprint sd: StaticBlueprint.values())
        	builder.addBlueprint(sd);
        for (EnumBlueprint ed: EnumBlueprint.values())
        	builder.addBlueprint(ed);
    }

    @Test
    public void testAppRunner1LowerCaseQwerty() {
        AbstractAppRunner.expectedMessage = "qwerty";
        AbstractAppRunner.expectedEnvironment = "production";
        AbstractAppRunner.expectedRunner = AppRunner1.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
        AbstractAppRunner.expectedDblValue = 321;
        AbstractAppRunner.expectedManualBean = 975;
        
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("lowercase", "qwerty", "AppRunner1", "production");
        builder.addBlueprint(new DuplicationBlueprint("a", 123, 1.23));
        builder.addBlueprint(new DuplicationBlueprint("b", 234, 2.34));
        builder.addBlueprint(new DuplicationBlueprint("c", 345, 3.45));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.registerBean(new ManualBean(975), new Descriptor<>(ManualBean.class));
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }

    @Test
    public void testAppRunner1UpperCaseQwerty() {
        AbstractAppRunner.expectedMessage = "QWERTY";
        AbstractAppRunner.expectedEnvironment = "production";
        AbstractAppRunner.expectedRunner = AppRunner1.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
        AbstractAppRunner.expectedDblValue = 123;
        
        System.setProperty("testProperty", "");
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("uppercase", "qwerty", "AppRunner1", "production");
        builder.addBlueprint(new DuplicationBlueprint("a", 123, 1.23));
        builder.addBlueprint(new DuplicationBlueprint("b", 234, 2.34));
        builder.addBlueprint(new DuplicationBlueprint("c", 345, 3.45));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
        System.clearProperty("testProperty");
    }

    @Test
    public void testAppRunner1DuplicateDynamicBlueprintName() {
        AbstractAppRunner.expectedMessage = "qwerty";
        AbstractAppRunner.expectedEnvironment = "production";
        AbstractAppRunner.expectedRunner = AppRunner1.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
        AbstractAppRunner.expectedDblValue = 321;
        
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("lowercase", "qwerty", "AppRunner1", "production");
        builder.addBlueprint(new DuplicationBlueprint("a", 123, 1.23));
        builder.addBlueprint(new DuplicationBlueprint("a", 123, 1.23));
        addEnumBlueprints(builder);
        Assertions.assertThrows(TendrilStartupException.class, () -> builder.build());

        // Everything should fail before any beans are created
        SingletonClass.assertNever();
        FactoryClass.assertNever();
        TempManager.assertNever();
        AbstractAppRunner.assertNever();
    }

    @Test
    public void testAppRunner2LowercaseQwerty() {
        AbstractAppRunner.expectedMessage = "qwerty";
        AbstractAppRunner.expectedEnvironment = "test";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        AbstractAppRunner.expectedDblValue = 123;

        System.setProperty("testProperty", "");
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("lowercase", "qwerty", "test");
        builder.addBlueprint(new DuplicationBlueprint("d", 321, 3.21));
        builder.addBlueprint(new DuplicationBlueprint("e", 432, 4.32));
        builder.addBlueprint(new DuplicationBlueprint("f", 543, 5.43));
        builder.addBlueprint(new DuplicationBlueprint("g", 654, 6.54));
        builder.addBlueprint(new DuplicationBlueprint("h", 765, 7.65));
        builder.addBlueprint(new DuplicationBlueprint("i", 876, 8.76));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        builder.addBlueprint(new ClassBlueprint("t3"));
        builder.addBlueprint(new ClassBlueprint("t4"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
        System.clearProperty("testProperty");
    }

    @Test
    public void testAppRunner2UppercaseQwerty() {
        AbstractAppRunner.expectedMessage = "QWERTY";
        AbstractAppRunner.expectedEnvironment = "test";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        AbstractAppRunner.expectedDblValue = 321;
        AbstractAppRunner.expectedManualBean = 786;
        
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("uppercase", "qwerty", "test");
        builder.addBlueprint(new DuplicationBlueprint("d", 321, 3.21));
        builder.addBlueprint(new DuplicationBlueprint("e", 432, 4.32));
        builder.addBlueprint(new DuplicationBlueprint("f", 543, 5.43));
        builder.addBlueprint(new DuplicationBlueprint("g", 654, 6.54));
        builder.addBlueprint(new DuplicationBlueprint("h", 765, 7.65));
        builder.addBlueprint(new DuplicationBlueprint("i", 876, 8.76));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        builder.addBlueprint(new ClassBlueprint("t3"));
        builder.addBlueprint(new ClassBlueprint("t4"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.registerBean(new ManualBean(786), new Descriptor<>(ManualBean.class));
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }

    @Test
    public void testAppRunner2LowercaseAbc123() {
        AbstractAppRunner.expectedMessage = "abc123";
        AbstractAppRunner.expectedEnvironment = "test";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        AbstractAppRunner.expectedDblValue = 3.21;
        
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("lowercase", "abc123", "test");
        builder.addBlueprint(new DuplicationBlueprint("d", 321, 3.21));
        builder.addBlueprint(new DuplicationBlueprint("e", 432, 4.32));
        builder.addBlueprint(new DuplicationBlueprint("f", 543, 5.43));
        builder.addBlueprint(new DuplicationBlueprint("g", 654, 6.54));
        builder.addBlueprint(new DuplicationBlueprint("h", 765, 7.65));
        builder.addBlueprint(new DuplicationBlueprint("i", 876, 8.76));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        builder.addBlueprint(new ClassBlueprint("t3"));
        builder.addBlueprint(new ClassBlueprint("t4"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }

    @Test
    public void testAppRunner2UppercaseAbc123() {
        AbstractAppRunner.expectedMessage = "ABC!@#";
        AbstractAppRunner.expectedEnvironment = "test";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        AbstractAppRunner.expectedDblValue = 1.23;
        AbstractAppRunner.expectedManualBean = 111;

        System.setProperty("testProperty", "");
        ApplicationContextBuilder builder = new ApplicationContextBuilder();
        builder.setEnvironments("uppercase", "abc123", "test");
        builder.addBlueprint(new DuplicationBlueprint("d", 321, 3.21));
        builder.addBlueprint(new DuplicationBlueprint("e", 432, 4.32));
        builder.addBlueprint(new DuplicationBlueprint("f", 543, 5.43));
        builder.addBlueprint(new DuplicationBlueprint("g", 654, 6.54));
        builder.addBlueprint(new DuplicationBlueprint("h", 765, 7.65));
        builder.addBlueprint(new DuplicationBlueprint("i", 876, 8.76));
        builder.addBlueprint(new ClassBlueprint("t1"));
        builder.addBlueprint(new ClassBlueprint("t2"));
        builder.addBlueprint(new ClassBlueprint("t3"));
        builder.addBlueprint(new ClassBlueprint("t4"));
        addEnumBlueprints(builder);
        ApplicationContext ctx = builder.build();
        ctx.registerBean(new ManualBean(111), new Descriptor<>(ManualBean.class));
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
        System.clearProperty("testProperty");
    }
}
