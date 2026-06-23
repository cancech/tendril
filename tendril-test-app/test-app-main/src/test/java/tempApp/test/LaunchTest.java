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
import tempApp.ClassDuplicate;
import tempApp.DuplicationDetails;
import tempApp.FactoryClass;
import tempApp.ManualBean;
import tempApp.MultiEnvBean1;
import tempApp.MultiEnvBean2;
import tempApp.RunnableConfig;
import tempApp.SingletonClass;
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
        builder.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
        builder.addDynamicBlueprint(new DuplicationDetails("b", 234, 2.34));
        builder.addDynamicBlueprint(new DuplicationDetails("c", 345, 3.45));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
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
        builder.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
        builder.addDynamicBlueprint(new DuplicationDetails("b", 234, 2.34));
        builder.addDynamicBlueprint(new DuplicationDetails("c", 345, 3.45));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
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
        builder.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
        builder.addDynamicBlueprint(new DuplicationDetails("a", 123, 1.23));
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
        builder.addDynamicBlueprint(new DuplicationDetails("d", 321, 3.21));
        builder.addDynamicBlueprint(new DuplicationDetails("e", 432, 4.32));
        builder.addDynamicBlueprint(new DuplicationDetails("f", 543, 5.43));
        builder.addDynamicBlueprint(new DuplicationDetails("g", 654, 6.54));
        builder.addDynamicBlueprint(new DuplicationDetails("h", 765, 7.65));
        builder.addDynamicBlueprint(new DuplicationDetails("i", 876, 8.76));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
        builder.addDynamicBlueprint(new ClassDuplicate("t3"));
        builder.addDynamicBlueprint(new ClassDuplicate("t4"));
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
        builder.addDynamicBlueprint(new DuplicationDetails("d", 321, 3.21));
        builder.addDynamicBlueprint(new DuplicationDetails("e", 432, 4.32));
        builder.addDynamicBlueprint(new DuplicationDetails("f", 543, 5.43));
        builder.addDynamicBlueprint(new DuplicationDetails("g", 654, 6.54));
        builder.addDynamicBlueprint(new DuplicationDetails("h", 765, 7.65));
        builder.addDynamicBlueprint(new DuplicationDetails("i", 876, 8.76));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
        builder.addDynamicBlueprint(new ClassDuplicate("t3"));
        builder.addDynamicBlueprint(new ClassDuplicate("t4"));
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
        builder.addDynamicBlueprint(new DuplicationDetails("d", 321, 3.21));
        builder.addDynamicBlueprint(new DuplicationDetails("e", 432, 4.32));
        builder.addDynamicBlueprint(new DuplicationDetails("f", 543, 5.43));
        builder.addDynamicBlueprint(new DuplicationDetails("g", 654, 6.54));
        builder.addDynamicBlueprint(new DuplicationDetails("h", 765, 7.65));
        builder.addDynamicBlueprint(new DuplicationDetails("i", 876, 8.76));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
        builder.addDynamicBlueprint(new ClassDuplicate("t3"));
        builder.addDynamicBlueprint(new ClassDuplicate("t4"));
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
        builder.addDynamicBlueprint(new DuplicationDetails("d", 321, 3.21));
        builder.addDynamicBlueprint(new DuplicationDetails("e", 432, 4.32));
        builder.addDynamicBlueprint(new DuplicationDetails("f", 543, 5.43));
        builder.addDynamicBlueprint(new DuplicationDetails("g", 654, 6.54));
        builder.addDynamicBlueprint(new DuplicationDetails("h", 765, 7.65));
        builder.addDynamicBlueprint(new DuplicationDetails("i", 876, 8.76));
        builder.addDynamicBlueprint(new ClassDuplicate("t1"));
        builder.addDynamicBlueprint(new ClassDuplicate("t2"));
        builder.addDynamicBlueprint(new ClassDuplicate("t3"));
        builder.addDynamicBlueprint(new ClassDuplicate("t4"));
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
