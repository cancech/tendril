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

import java.util.ArrayList;
import java.util.List;

import tempApp.duplicate.StaticDuplicateBean;
import tempApp.duplicate.StaticDuplicateBean2;
import tempApp.duplicate.StaticDuplicateBean3;
import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;
import tendril.context.launch.TendrilRunner;

public abstract class AbstractAppRunner implements TendrilRunner {
    public static String expectedMessage = "must be set by main";
    public static Class<? extends AbstractAppRunner> expectedRunner;
    public static Class<? extends MultiEnvBean> expectedMultiEnvBean;
    
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
    @Sibling
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
    @Inject
    MultiEnvBean multiEnvBean;

    // To test injections from PriorityConfig
    @Inject
    @Option1
    StringWrapper option1StringWrapper;
    @Inject
    @Option2
    StringWrapper option2StringWrapper;
    @Inject
    @Named("Option1")
    StringWrapper option1NamedStringWrapper;
    @Inject
    @Named("Option2")
    StringWrapper option2NamedStringWrapper;
    @InjectAll
    List<StringWrapper> allStringWrappers;
    @InjectAll
    @Option1
    List<StringWrapper> allOption1StringWrappers;
    @InjectAll
    @Option2
    List<StringWrapper> allOption2StringWrappers;
    @InjectAll
    @Option1
    @Option2
    List<StringWrapper> allOption1and2StringWrappers;
    @InjectAll
    @Message
    List<StringWrapper> allMessageStringWrapeprs;

    @Inject
    @COPY_1
    StaticDuplicateBean bean1Copy1;
    @Inject
    @COPY_2
    StaticDuplicateBean bean1Copy2;
    @Inject
    @COPY_3
    StaticDuplicateBean bean1Copy3;
    @InjectAll
    List<StaticDuplicateBean> allBean1Copies;
    @Inject
    @COPY_1
    StaticDuplicateBean2 bean2Copy1;
    @Inject
    @COPY_2
    StaticDuplicateBean2 bean2Copy2;
    @Inject
    @COPY_3
    StaticDuplicateBean2 bean2Copy3;
    @InjectAll
    List<StaticDuplicateBean2> allBean2Copies;
    @Inject
    @COPY_1
    StaticDuplicateBean3 bean3Copy1;
    @Inject
    @COPY_2
    StaticDuplicateBean3 bean3Copy2;
    @Inject
    @COPY_3
    StaticDuplicateBean3 bean3Copy3;
    @InjectAll
    List<StaticDuplicateBean3> allBean3Copies;
    
    private FactoryClass factoryBean5;
    private final Class<? extends AbstractAppRunner> actualRunner;

    AbstractAppRunner(Class<? extends AbstractAppRunner> concreteRunner) {
        this.actualRunner = concreteRunner;
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
        assertion(expectedRunner == actualRunner, "Runner expected to be " + expectedRunner + " but was " + actualRunner);

        System.out.println("RUNNING " + actualRunner.getSimpleName()  + "!!!");
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

        assertion(multiEnvBean != null, "MultiEnvBean was not created!");
        assertion(multiEnvBean.getClass() == expectedMultiEnvBean, "Expected " + expectedMultiEnvBean + " but received " + multiEnvBean.getClass());

        // Make sure the @Primary @Fallback is properly resolved
        assertion(PriorityConfig.PRIMARY1.equals(option1StringWrapper.getString()), "Expected " + PriorityConfig.PRIMARY1 + " but received " + option1StringWrapper.getString());
        assertion(PriorityConfig.PRIMARY2.equals(option2StringWrapper.getString()), "Expected " + PriorityConfig.PRIMARY2 + " but received " + option2StringWrapper.getString());
        assertion(PriorityConfig.FALLBACK1.equals(option1NamedStringWrapper.getString()), "Expected " + PriorityConfig.FALLBACK1 + " but received " + option1NamedStringWrapper.getString());
        assertion(PriorityConfig.FALLBACK2.equals(option2NamedStringWrapper.getString()), "Expected " + PriorityConfig.FALLBACK2 + " but received " + option2NamedStringWrapper.getString());
        assertStringWrapperListContains(allStringWrappers, PriorityConfig.PRIMARY1, PriorityConfig.PRIMARY2, PriorityConfig.BASIC1, PriorityConfig.BASIC2, PriorityConfig.BASIC3);
        assertStringWrapperListContains(allOption1StringWrappers, PriorityConfig.PRIMARY1, PriorityConfig.BASIC1, PriorityConfig.BASIC2, PriorityConfig.BASIC3);
        assertStringWrapperListContains(allOption2StringWrappers, PriorityConfig.PRIMARY2, PriorityConfig.BASIC1, PriorityConfig.BASIC2, PriorityConfig.BASIC3);
        assertStringWrapperListContains(allOption1and2StringWrappers, PriorityConfig.BASIC1, PriorityConfig.BASIC2, PriorityConfig.BASIC3);
        assertStringWrapperListContains(allMessageStringWrapeprs, PriorityConfig.FALLBACK3);
        
        // Make sure the Enum driven @Duplicate bean1 is properly resolved
        assertion(bean1Copy1.isSameBlueprint(), "Expected instance field and constructor blueprint to be the same");
        assertion(bean1Copy1.getInteger() == 1, "Expected 1 but received " + bean1Copy1.getInteger());
        assertion(bean1Copy1.getDouble() == 1.23, "Expected 1.23 but received " + bean1Copy1.getDouble());
        assertion("First".equals(bean1Copy1.getString()), "Expected First but received " + bean1Copy1.getString());
        assertion(bean1Copy1.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean1Copy1.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean1Copy1.getMessage());

        assertion(bean1Copy2.isSameBlueprint(), "Expected instance field and constructor blueprint to be the same");
        assertion(bean1Copy2.getInteger() == 2, "Expected 2 but received " + bean1Copy2.getInteger());
        assertion(bean1Copy2.getDouble() == 2.34, "Expected 2.34 but received " + bean1Copy2.getDouble());
        assertion("Second".equals(bean1Copy2.getString()), "Expected Second but received " + bean1Copy2.getString());
        assertion(bean1Copy2.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean1Copy2.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean1Copy2.getMessage());

        assertion(bean1Copy3.isSameBlueprint(), "Expected instance field and constructor blueprint to be the same");
        assertion(bean1Copy3.getInteger() == 3, "Expected 3 but received " + bean1Copy3.getInteger());
        assertion(bean1Copy3.getDouble() == 3.45, "Expected 3.45 but received " + bean1Copy3.getDouble());
        assertion("Third".equals(bean1Copy3.getString()), "Expected Third but received " + bean1Copy3.getString());
        assertion(bean1Copy3.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean1Copy3.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean1Copy3.getMessage());
        
        assertion(allBean1Copies.size() == 3, "Expected to receive three copies, but received " + allBean1Copies.size());
        assertion(allBean1Copies.contains(bean1Copy1), "bean1Copy1 is not contained in allBean1Copies");
        assertion(allBean1Copies.contains(bean1Copy2), "bean1Copy2 is not contained in allBean1Copies");
        assertion(allBean1Copies.contains(bean1Copy3), "bean1Copy3 is not contained in allCoallBean1Copiespies");

        // Make sure the Enum driven @Duplicate bean2 is properly resolved
        assertion(bean2Copy1.getInteger() == 1, "Expected 1 but received " + bean2Copy1.getInteger());
        assertion(bean2Copy1.getDouble() == 1.23, "Expected 1.23 but received " + bean2Copy1.getDouble());
        assertion("First".equals(bean2Copy1.getString()), "Expected First but received " + bean2Copy1.getString());
        assertion(bean2Copy1.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean2Copy1.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean2Copy1.getMessage());

        assertion(bean2Copy2.getInteger() == 2, "Expected 2 but received " + bean2Copy2.getInteger());
        assertion(bean2Copy2.getDouble() == 2.34, "Expected 2.34 but received " + bean2Copy2.getDouble());
        assertion("Second".equals(bean2Copy2.getString()), "Expected Second but received " + bean2Copy2.getString());
        assertion(bean2Copy2.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean2Copy2.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean2Copy2.getMessage());

        assertion(bean2Copy3.getInteger() == 3, "Expected 3 but received " + bean2Copy3.getInteger());
        assertion(bean2Copy3.getDouble() == 3.45, "Expected 3.45 but received " + bean2Copy3.getDouble());
        assertion("Third".equals(bean2Copy3.getString()), "Expected Third but received " + bean2Copy3.getString());
        assertion(bean2Copy3.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean2Copy3.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean2Copy3.getMessage());
        
        assertion(allBean2Copies.size() == 3, "Expected to receive three copies, but received " + allBean2Copies.size());
        assertion(!allBean2Copies.contains(bean2Copy1), "bean2Copy1 is contained in allBean2Copies");
        assertion(!allBean2Copies.contains(bean2Copy2), "bean2Copy2 is contained in allBean2Copies");
        assertion(!allBean2Copies.contains(bean2Copy3), "bean2Copy3 is contained in allBean2Copies");
        for (StaticDuplicateBean2 b2: allBean2Copies)
        	assertion(b2.isEquivalent(bean2Copy1) || b2.isEquivalent(bean2Copy2) || b2.isEquivalent(bean2Copy3), "allBean2Copies contains unexpecte bean " + b2.getBlueprint());

        // Make sure the Enum driven @Duplicate bean3 is properly resolved
        assertion(bean3Copy1.getInteger() == 1, "Expected 1 but received " + bean3Copy1.getInteger());
        assertion(bean3Copy1.getDouble() == 1.23, "Expected 1.23 but received " + bean3Copy1.getDouble());
        assertion("First".equals(bean3Copy1.getString()), "Expected First but received " + bean3Copy1.getString());
        assertion(bean3Copy1.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean3Copy1.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean3Copy1.getMessage());

        assertion(bean3Copy2.getInteger() == 2, "Expected 2 but received " + bean3Copy2.getInteger());
        assertion(bean3Copy2.getDouble() == 2.34, "Expected 2.34 but received " + bean3Copy2.getDouble());
        assertion("Second".equals(bean3Copy2.getString()), "Expected Second but received " + bean3Copy2.getString());
        assertion(bean3Copy2.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean3Copy2.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean3Copy2.getMessage());

        assertion(bean3Copy3.getInteger() == 3, "Expected 3 but received " + bean3Copy3.getInteger());
        assertion(bean3Copy3.getDouble() == 3.45, "Expected 3.45 but received " + bean3Copy3.getDouble());
        assertion("Third".equals(bean3Copy3.getString()), "Expected Third but received " + bean3Copy3.getString());
        assertion(bean3Copy3.isSameMessage(), "Expected instance field and constructor messages to be the same");
        assertion(bean3Copy3.getMessage().equals(expectedMessage), "Expected message " + expectedMessage + " but received " + bean3Copy3.getMessage());
        
        assertion(allBean3Copies.size() == 3, "Expected to receive three copies, but received " + allBean3Copies.size());
        assertion(allBean3Copies.contains(bean3Copy1), "bean3Copy1 is not contained in allBean3Copies");
        assertion(allBean3Copies.contains(bean3Copy2), "bean3Copy2 is not contained in allBean3Copies");
        assertion(allBean3Copies.contains(bean3Copy3), "bean3Copy3 is not contained in allBean3Copies");
        
        // Make sure that the correct siblings have been provided
        assertion(bean1Copy1.isCorrectSibling(), "Wrong sibling provided to bean1Copy1");
        assertion(bean1Copy2.isCorrectSibling(), "Wrong sibling provided to bean1Copy2");
        assertion(bean1Copy3.isCorrectSibling(), "Wrong sibling provided to bean1Copy3");

        assertion(bean3Copy1.isCorrectSibling(), "Wrong sibling provided to bean3Copy1");
        assertion(bean3Copy1.isCorrectBean(bean1Copy1), "Wrong sibling provided to bean3Copy1");
        assertion(bean3Copy2.isCorrectSibling(), "Wrong sibling provided to bean3Copy2");
        assertion(bean3Copy2.isCorrectBean(bean1Copy2), "Wrong sibling provided to bean3Copy2");
        assertion(bean3Copy3.isCorrectSibling(), "Wrong sibling provided to bean3Copy3");
        assertion(bean3Copy3.isCorrectBean(bean1Copy3), "Wrong sibling provided to bean3Copy3");
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
    
    private void assertStringWrapperListContains(List<StringWrapper> actual, String...expected) {
        // Ensure that the list is correct
        assertion(actual.size() == expected.length, "Expected " + expected.length + " elements, but recevied " + actual.size());
        
        List<String> actualStrings = new ArrayList<String>();
        for (StringWrapper sw: actual)
        	actualStrings.add(sw.getString());
        
        for (String e: expected)
            assertion(actualStrings.contains(e), "List " + actualStrings + " does not contain element " + e);
    }
    
}
