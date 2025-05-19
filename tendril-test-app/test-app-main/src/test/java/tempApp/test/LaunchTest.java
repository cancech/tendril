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

import tempApp.AppRunner;
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
        AppRunner.reset();
    }

    @Test
    public void testLowercaseQwerty() {
        AppRunner.expectedMessage = "qwerty";
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "qwerty");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner.assertSingleton();
    }

    @Test
    public void testUppercaseQwerty() {
        AppRunner.expectedMessage = "QWERTY";
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "qwerty");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner.assertSingleton();
    }

    @Test
    public void testLowercaseAbc123() {
        AppRunner.expectedMessage = "abc123";
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("lowercase", "abc123");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner.assertSingleton();
    }

    @Test
    public void testUppercaseAbc123() {
        AppRunner.expectedMessage = "ABC!@#";
        
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnvironments("uppercase", "abc123");
        ctx.start();
        
        // Ensure everything created the expected number of times
        SingletonClass.assertSingleton();
        FactoryClass.assertFactory();
        TempManager.assertSingleton();
        AppRunner.assertSingleton();
    }
}
