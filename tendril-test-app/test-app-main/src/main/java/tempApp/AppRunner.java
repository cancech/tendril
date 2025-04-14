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
package tempApp;

import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Inject;
import tendril.bean.qualifier.Named;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;

@Runner
public class AppRunner implements TendrilRunner {

    private static int instances = 0;
    private static int timesDoSomething = 0;
    private static int timesDoSomethingElse = 0;
    private static int timesDoNothing = 0;
    private static int timesEnumInjector = 0;
    private static int timesRun = 0;
    
    public static void assertSingleton() {
        assert(instances == 1);
        assert(timesDoSomething == 1);
        assert(timesDoSomethingElse == 1);
        assert(timesDoNothing == 1);
        assert(timesEnumInjector == 1);
        assert(timesRun == 1);
    }
    
    public static void reset() {
        instances = 0;
        timesDoSomething = 0;
        timesDoSomethingElse = 0;
        timesDoNothing = 0;
        timesEnumInjector = 0;
        timesRun = 0;
    }
    
    
    @Inject
    @Named("TempName")
    Object tmpClass;

    @Inject
    FactoryClass factoryBean1;
    @Inject
    FactoryClass factoryBean2;
    @Inject
    FactoryClass factoryBean3;
    @Inject
    FactoryClass factoryBean4;
    @Inject
    @Named("TempString")
    String tempString1;
    @Inject
    @MyTypeId(MyType.VAL1)
    String tempString2;
    @Inject
    @MyTypeId(MyType.VAL2)
    Object objVal2;
    @Inject
    @TempQualifier
    Object objTmp;
    @Inject
    @Option1
    Runnable singletonRunnable;
    @Inject
    @Option2
    Runnable option2Runnable;
    @Inject
    @Named("second")
    Runnable secondRunnable;
    
    private FactoryClass factoryBean5;
    
    AppRunner() {
        instances++;
    }
    
    @Inject
    void doSomething(SingletonClass singleton1, SingletonClass singleton2, FactoryClass factory1, FactoryClass factory2, FactoryClass factory3) {
        timesDoSomething++;
        System.out.println("doSomething()");
        factoryBean5 = factory1;

        System.out.println(factory1);
        System.out.println(factory2);
        System.out.println(factory3);
        
        assert(singleton1 == singleton2);
        assert(factory1 != factory2);
        assert(factory1 != factory3);
        assert(factory2 != factory3);
    }
    
    @Inject
    void doSomethingElse(FactoryClass factory1) {
        timesDoSomethingElse++;
        System.out.println("doSomethingElse()");
        System.out.println(factory1);
    }
    
    @Inject
    void doNothing() {
        timesDoNothing++;
        System.out.println("doNothing()");
    }
    
    @Inject
    void enumInjector(@MyTypeId(MyType.VAL1) String str) {
        timesEnumInjector++;
        System.out.println("INJECTED VIA ENUM: " + str);
    }

    @Override
    public void run() {
        timesRun++;
        System.out.println("RUNNING!!! " + tmpClass);
        System.out.println(factoryBean1);
        System.out.println(factoryBean2);
        System.out.println(factoryBean3);
        System.out.println(factoryBean4);
        System.out.println(factoryBean5);
        System.out.println(tempString1);
        System.out.println(tempString2);
        System.out.println("Objects: " + objVal2 + ", " + objTmp);
        assert(tempString1.equals(tempString2));
        assert(factoryBean1 != factoryBean2);
        assert(factoryBean1 != factoryBean3);
        assert(factoryBean1 != factoryBean4);
        assert(factoryBean1 != factoryBean5);
        assert(factoryBean2 != factoryBean3);
        assert(factoryBean2 != factoryBean4);
        assert(factoryBean2 != factoryBean5);
        assert(factoryBean3 != factoryBean4);
        assert(factoryBean3 != factoryBean5);
        assert(factoryBean4 != factoryBean5);
        assert(objVal2 != objTmp);
        
        assert(singletonRunnable != option2Runnable);
        assert(singletonRunnable != secondRunnable);
        assert(option2Runnable != secondRunnable);
        singletonRunnable.run();
        option2Runnable.run();
        secondRunnable.run();
    }
}
