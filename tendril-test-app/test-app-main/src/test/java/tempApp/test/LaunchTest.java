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

import tempApp.AppRunner1;
import tempApp.AppRunner2;
import tempApp.FactoryClass;
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
        AppRunner1.reset();
        AppRunner2.reset();
    }

    @Test
    public void testAppRunner1LowerCaseQwerty() {
        AppRunner1.expectedMessage = "qwerty";
        AppRunner1.expectedRunner = AppRunner1.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "qwerty", "AppRunner1");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner1.assertSingleton();
    }

    @Test
    public void testAppRunner1UpperCaseQwerty() {
        AppRunner1.expectedMessage = "QWERTY";
        AppRunner1.expectedRunner = AppRunner1.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty", "AppRunner1");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner1.assertSingleton();
    }

    @Test
    public void testAppRunner2LowercaseQwerty() {
        AppRunner2.expectedMessage = "qwerty";
        AppRunner2.expectedRunner = AppRunner2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "qwerty");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner2.assertSingleton();
    }

    @Test
    public void testAppRunner2UppercaseQwerty() {
        AppRunner2.expectedMessage = "QWERTY";
        AppRunner2.expectedRunner = AppRunner2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner2.assertSingleton();
    }

    @Test
    public void testAppRunner2LowercaseAbc123() {
        AppRunner2.expectedMessage = "abc123";
        AppRunner2.expectedRunner = AppRunner2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "abc123");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner2.assertSingleton();
    }

    @Test
    public void testAppRunner2UppercaseAbc123() {
        AppRunner2.expectedMessage = "ABC!@#";
        AppRunner2.expectedRunner = AppRunner2.class;
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "abc123");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner2.assertSingleton();
    }
}
