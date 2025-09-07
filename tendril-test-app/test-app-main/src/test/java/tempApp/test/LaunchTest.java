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
import org.junit.jupiter.api.Test;

import tempApp.AbstractAppRunner;
import tempApp.AppRunner1;
import tempApp.AppRunner2;
import tempApp.FactoryClass;
import tempApp.MultiEnvBean1;
import tempApp.MultiEnvBean2;
import tempApp.SingletonClass;
import tempApp.TempManager;
import tendril.context.ApplicationContext;

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
        AbstractAppRunner.reset();
    }

    @Test
    public void testAppRunner1LowerCaseQwerty() {
        AbstractAppRunner.expectedMessage = "qwerty";
        AbstractAppRunner.expectedRunner = AppRunner1.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "qwerty", "AppRunner1", "production");
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
        AbstractAppRunner.expectedRunner = AppRunner1.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean1.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "AppRunner1", "production");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }

    @Test
    public void testAppRunner2LowercaseQwerty() {
        AbstractAppRunner.expectedMessage = "qwerty";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "qwerty", "test");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }

    @Test
    public void testAppRunner2UppercaseQwerty() {
        AbstractAppRunner.expectedMessage = "QWERTY";
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "test");
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
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "abc123", "test");
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
        AbstractAppRunner.expectedRunner = AppRunner2.class;
        AbstractAppRunner.expectedMultiEnvBean = MultiEnvBean2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "abc123", "test");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AbstractAppRunner.assertSingleton();
    }
}
