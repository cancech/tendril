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

import java.util.List;

import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.qualifier.Named;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;

@Runner
public class AppRunner implements TendrilRunner {

    public static String expectedMessage = "must be set by main";
    
    private static int instances = 0;
    private static int timesDoSomething = 0;
    private static int timesDoSomethingElse = 0;
    private static int timesDoNothing = 0;
    private static int timesEnumInjector = 0;
    private static int timesRun = 0;
    private static int timesAllInjectorRun = 0;

    public static void assertSingleton() {
        assertion(instances == 1, "instances should be 1, but was " + instances);
        assertion(timesDoSomething == 1, "instances should be 1, but was " + timesDoSomething);
        assertion(timesDoSomethingElse == 1, "instances should be 1, but was " + timesDoSomethingElse);
        assertion(timesDoNothing == 1, "instances should be 1, but was " + timesDoNothing);
        assertion(timesEnumInjector == 1, "instances should be 1, but was " + timesEnumInjector);
        assertion(timesRun == 1, "instances should be 1, but was " + timesRun);
        assertion(timesAllInjectorRun == 1, "instances should be 1, but was " + timesAllInjectorRun);
    }

    public static void reset() {
        instances = 0;
        timesDoSomething = 0;
        timesDoSomethingElse = 0;
        timesDoNothing = 0;
        timesEnumInjector = 0;
        timesRun = 0;
        timesAllInjectorRun = 0;
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
    @Inject
    @Named("notenv")
    Runnable notEnvRunnable;
    @InjectAll
    List<? extends Runnable> allRunnables;
    @Inject
    @Message
    String message;

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

        assert (singleton1 == singleton2);
        assert (factory1 != factory2);
        assert (factory1 != factory3);
        assert (factory2 != factory3);
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
    
    @Inject
    void allInjector(@InjectAll List<? extends Runnable> all, @Option1 Runnable one, @Option2 Runnable two, @Named("second") Runnable three, @Named("notenv") Runnable four) {
        timesAllInjectorRun++;
        assertRunnableList("allInjector", all, one, two, three, four);
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
        assertion(tempString1.equals(tempString2), "Ex[ected \"" + tempString1 + "\", but received \"" + tempString2 + "\"");
        assertion(factoryBean1 != factoryBean2, "factoryBean1 == factoryBean2");
        assertion(factoryBean1 != factoryBean3, "factoryBean1 == factoryBean3");
        assertion(factoryBean1 != factoryBean4, "factoryBean1 == factoryBean4");
        assertion(factoryBean1 != factoryBean5, "factoryBean1 == factoryBean5");
        assertion(factoryBean2 != factoryBean3, "factoryBean2 == factoryBean3");
        assertion(factoryBean2 != factoryBean4, "factoryBean2 == factoryBean4");
        assertion(factoryBean2 != factoryBean5, "factoryBean2 == factoryBean5");
        assertion(factoryBean3 != factoryBean4, "factoryBean3 == factoryBean4");
        assertion(factoryBean3 != factoryBean5, "factoryBean3 == factoryBean5");
        assertion(factoryBean4 != factoryBean5, "factoryBean4 == factoryBean5");
        assertion(objVal2 != objTmp, "objVal2 == objTmp");

        assertion(singletonRunnable != option2Runnable, "singletonRunnable == option2Runnable");
        assertion(singletonRunnable != secondRunnable, "singletonRunnable == secondRunnable");
        assertion(singletonRunnable != notEnvRunnable, "singletonRunnable == notEnvRunnable");
        assertion(option2Runnable != secondRunnable, "option2Runnable == secondRunnable");
        assertion(option2Runnable != notEnvRunnable, "option2Runnable == notEnvRunnable");
        assertion(secondRunnable != notEnvRunnable, "secondRunnable == notEnvRunnable");
        assertRunnableList("Instance Fields", allRunnables, singletonRunnable, option2Runnable, secondRunnable, notEnvRunnable);
        
        System.out.println("MESSAGE IS: " + message);
        assertion(message.equals(expectedMessage), "Expected \"" + expectedMessage + "\", but received \"" + message + "\"");
    }

    private  static void assertion(boolean value, String msg) {
        if (!value)
            throw new AssertionError(msg);
    }
    
    private void assertRunnableList(String annoucement, List<? extends Runnable> list, Runnable...runnables) {
        assertListContains(list, runnables);
        
        System.out.println("---------------------------- Running List " + annoucement + " ----------------------------");
        for (Runnable r: runnables)
            r.run();
        System.out.println("-------------------------------------------------------------------------------");
    }
    
    @SafeVarargs
    private <T> void assertListContains(List<? extends T> list, T...elements) {
        // Ensure that the list is correct
        assertion(list.size() == elements.length, "Expected " + elements.length + " elements, but recevied " + list.size());
        for (T e: elements)
            assertion(list.contains(e), "List " + list + " does not contain element " + e);
    }
}
