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
package tempApp.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import tempApp.DuplicationBlueprint;
import tempApp.EnumBlueprint;
import tempApp.ExceptionCtorBean;
import tempApp.FactoryClass;
import tempApp.GenericWrapper;
import tempApp.IntWrapper;
import tempApp.IntWrapperImpl;
import tempApp.Lib2DataStruct;
import tempApp.Lib2StaticBean;
import tempApp.ManualBean;
import tempApp.Message;
import tempApp.MultiEnvBean;
import tempApp.Option1;
import tempApp.Option2;
import tempApp.PrimitiveGenericWrapperConsumer;
import tempApp.PriorityConfig;
import tempApp.ReplaceIntWrapper;
import tempApp.RunnableConfig;
import tempApp.SingletonClass;
import tempApp.StaticBlueprint;
import tempApp.StringWrapper;
import tempApp.TempQualifier;
import tempApp.duplicate.DynamicDuplicate;
import tempApp.duplicate.Lib1DuplicateBean;
import tempApp.duplicate.Printer;
import tempApp.duplicate.StaticDuplicateBean;
import tempApp.duplicate.StaticDuplicateBean2;
import tempApp.duplicate.StaticDuplicateBean3;
import tempApp.duplicate.StringInterface;
import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tempApp.lib1dup.ParentDuplicate;
import tempApp.lib1orig.Original;
import tempApp.lib1orig.OriginalNamed;
import tempApp.lib1orig.OriginalOption1;
import tempApp.lib1replace.Replace;
import tempApp.lib1replace.ReplaceNamed;
import tempApp.lib1replace.ReplaceOption1;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.qualifier.Named;
import tendril.codegen.field.type.PrimitiveType;
import tendril.context.ApplicationContext;
import tendril.context.launch.TendrilRunner;

public abstract class AbstractAppRunner implements TendrilRunner {
	public static String expectedMessage = "must be set by main";
	public static String expectedEnvironment = "must be set by main";
	public static double expectedDblValue = -1;
	public static Class<? extends AbstractAppRunner> expectedRunner;
	public static Class<? extends MultiEnvBean> expectedMultiEnvBean;
	public static int expectedManualBean = -1;

	private static int instances = 0;
	private static int timesDoSomething = 0;
	private static int timesDoSomethingElse = 0;
	private static int timesDoNothing = 0;
	private static int timesEnumInjector = 0;
	private static int timesRun = 0;
	private static int timesAllInjectorRun = 0;

	public static void assertNever() {
		assertion(instances == 0, "instances should be 0, but was " + instances);
		assertion(timesDoSomething == 0, "instances should be 0, but was " + timesDoSomething);
		assertion(timesDoSomethingElse == 0, "instances should be 0, but was " + timesDoSomethingElse);
		assertion(timesDoNothing == 0, "instances should be 0, but was " + timesDoNothing);
		assertion(timesEnumInjector == 0, "instances should be 0, but was " + timesEnumInjector);
		assertion(timesRun == 0, "instances should be 0, but was " + timesRun);
		assertion(timesAllInjectorRun == 0, "instances should be 0, but was " + timesAllInjectorRun);
	}

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
		expectedManualBean = -1;
	}
	
	@Inject
	private PrimitiveGenericWrapperConsumer primitiveGenericWrapperConsumer;

	@Inject
	@Named("TempName")
	protected Object tmpClass;

	@Inject
	@Sibling
	FactoryClass factoryBean1;
	@Inject
	public FactoryClass factoryBean2;
	@Inject
	private FactoryClass factoryBean3;
	@Inject
	protected FactoryClass factoryBean4;
	@Inject
	@Named("TempString")
	String tempString1;
	@Inject
	@MyTypeId(MyType.VAL1)
	public String tempString2;
	@Inject
	@MyTypeId(MyType.VAL2)
	private Object objVal2;
	@Inject
	@TempQualifier
	protected Object objTmp;
	@Inject
	@Option1
	Runnable singletonRunnable;
	@Inject
	@Option2
	public Runnable option2Runnable;
	@Inject
	@Named("second")
	private Runnable secondRunnable;
	@Inject
	@Named("notenv")
	protected Runnable notEnvRunnable;
	@InjectAll
	List<? extends Runnable> allRunnables;
	@Inject
	@Message
	public String message;
	@Inject
	private MultiEnvBean multiEnvBean;

	// To test injections from PriorityConfig
	@Inject
	@Option1
	protected StringWrapper option1StringWrapper;
	@Inject
	@Option2
	StringWrapper option2StringWrapper;
	@Inject
	@Named("Option1")
	public StringWrapper option1NamedStringWrapper;
	@Inject
	@Named("Option2")
	private StringWrapper option2NamedStringWrapper;
	@InjectAll
	protected List<StringWrapper> allStringWrappers;
	@InjectAll
	@Option1
	List<StringWrapper> allOption1StringWrappers;
	@InjectAll
	@Option2
	public List<StringWrapper> allOption2StringWrappers;
	@InjectAll
	@Option1
	@Option2
	private List<StringWrapper> allOption1and2StringWrappers;
	@InjectAll
	@Message
	protected List<StringWrapper> allMessageStringWrapeprs;

	@Inject
	@Named("COPY_1")
	StaticDuplicateBean bean1Copy1;
	@Inject
	@Named("COPY_2")
	public StaticDuplicateBean bean1Copy2;
	@Inject
	@Named("COPY_3")
	private StaticDuplicateBean bean1Copy3;
	@InjectAll
	protected List<StaticDuplicateBean> allBean1Copies;
	@Inject
	@Named("COPY_1")
	StaticDuplicateBean2 bean2Copy1;
	@Inject
	@Named("COPY_2")
	public StaticDuplicateBean2 bean2Copy2;
	@Inject
	@Named("COPY_3")
	private StaticDuplicateBean2 bean2Copy3;
	@InjectAll
	protected List<StaticDuplicateBean2> allBean2Copies;
	@Inject
	@Named("COPY_1")
	StaticDuplicateBean3 bean3Copy1;
	@Inject
	@Named("COPY_2")
	public StaticDuplicateBean3 bean3Copy2;
	@Inject
	@Named("COPY_3")
	private StaticDuplicateBean3 bean3Copy3;
	@InjectAll
	protected List<StaticDuplicateBean3> allBean3Copies;

	@Inject
	@Named("COPY_1")
	StringInterface strIf1;
	@Inject
	@Named("COPY_2")
	public StringInterface strIf2;
	@Inject
	@Named("COPY_3")
	private StringInterface strIf3;
	@InjectAll
	protected List<StringInterface> strIfaces;
	
	@InjectAll
	List<Printer> allPrinters;

	@InjectAll
	public List<DynamicDuplicate> dynamicDuplicates;
	@InjectAll
	private List<ParentDuplicate> parentDuplicates;
	@InjectAll
	private List<Lib1DuplicateBean> lib1DuplicatesFromLib2;
	@InjectAll
	private List<Lib2StaticBean> lib2DuplicatesFromApp; 
	
	@Inject
	protected Original originalBean;
	@Inject
	Original originalBean2;
	@Inject
	@Option2
	public Original originalBeanOption2;
	@Inject
	@Option1
	private OriginalOption1 originalOption1Bean;
	@Inject
	@Option1
	protected OriginalOption1 originalOption1Bean2;
	@Inject
	@Named("originalNamed")
	OriginalNamed originalNamed;
	@Inject
	@Named("originalNamed")
	public OriginalNamed originalNamed2;
	
	@Inject
	private Lib2DataStruct dataStruct;

	@Inject
	@Message
	protected Double dblValue;
	
	@Inject
	ApplicationContext ctx;
	
	@Inject
	public List<GenericWrapper<PrimitiveType>> primitives;
	
	@Inject
	private ExceptionCtorBean exCtorBean;
	
	@InjectAll
	protected List<IntWrapper> allIntWrappers;
	@Inject
	@Named("standaloneIntWrapper")
	IntWrapper replacedStandaloneWrapper;
	@Inject
	@Named("configIntWrapper")
	public IntWrapper replacedConfigWrapper;
	
	@Inject
	private List<Integer> intList;
	@Inject
	protected List<String> stringList;

	private final int numOfClassDuplicates;
	private final DuplicationBlueprint[] expectedDynamicDuplicates;

	private FactoryClass factoryBean5;
	private final Class<? extends AbstractAppRunner> actualRunner;

	protected AbstractAppRunner(Class<? extends AbstractAppRunner> concreteRunner, int numOfClassDuplicates, DuplicationBlueprint... dynamicDuplicateDetails) {
		this.actualRunner = concreteRunner;
		this.numOfClassDuplicates = numOfClassDuplicates;
		this.expectedDynamicDuplicates = dynamicDuplicateDetails;
		instances++;
	}

	@Inject
	private void doSomething(SingletonClass singleton1, SingletonClass singleton2, FactoryClass factory1, FactoryClass factory2, FactoryClass factory3, List<Integer> intList) {
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
	protected void doSomethingElse(FactoryClass factory1) {
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
	public void enumInjector(@MyTypeId(MyType.VAL1) String str) {
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

		System.out.println("RUNNING " + actualRunner.getSimpleName() + "!!!");
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
		for (StaticDuplicateBean2 b2 : allBean2Copies)
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

		// Make sure that the config duplicates are properly received
		assertion("First".equals(strIf1.getValue()), "Expected First but received " + strIf1.getValue());
		assertion("Second".equals(strIf2.getValue()), "Expected Second but received " + strIf2.getValue());
		assertion("Third".equals(strIf3.getValue()), "Expected Third but received " + strIf3.getValue());

		assertion(strIfaces.size() == 3, "Expected to receive three copies, but received " + strIfaces.size());
		assertion(strIfaces.contains(strIf1), "strIf1 is not contained in strIfaces");
		assertion(strIfaces.contains(strIf2), "strIf2 is not contained in strIfaces");
		assertion(strIfaces.contains(strIf3), "strIf3 is not contained in strIfaces");

		// Make sure that the dynamic duplicates are all accounted for
		assertion(dynamicDuplicates.size() == expectedDynamicDuplicates.length, "Expected to receive " + expectedDynamicDuplicates.length + " copies, but received " + dynamicDuplicates.size());
		for (DuplicationBlueprint d : expectedDynamicDuplicates) {
			String dName = d.getName();
			boolean found = false;
			for (DynamicDuplicate dAll : dynamicDuplicates) {
				if (dName.equals(dAll.getName())) {
					found = true;
					assertion(d.getInt() == dAll.getInt(), "Mismatch for dynamic duplicate " + dName + " expected " + d.getInt() + " but got " + dAll.getInt());
					assertion(d.getDouble() == dAll.getDouble(), "Mismatch for dynamic duplicate " + dName + " expected " + d.getDouble() + " but got " + dAll.getDouble());
					assertion(dAll.getBean1() == bean1Copy1, "Mismatch for dynamic duplicate " + dName + " bean1 is different");
					assertion(dAll.getBean2() != bean2Copy2, "Mismatch for dynamic duplicate " + dName + " bean2 is the same");
					assertion(dAll.getBean2().isEquivalent(bean2Copy2), "Mismatch for dynamic duplicate " + dName + " bean2 is not equivalent");
					assertion(dAll.getBean3() == bean3Copy3, "Mismatch for dynamic duplicate " + dName + " bean3 is different");
				}
			}
			assertion(found, "Unable to find expected dynamic duplicate " + dName);
		}

		// Verify that the RunnableConfig is properly processed
		RunnableConfig.assertTimesValidateCalled(1);
		RunnableConfig.assertTimesMethodInjectCalled(1);
		
		// Verify the printers
		int expectedPrinters = expectedDynamicDuplicates.length + StaticBlueprint.values().length // What is explicitly provided individually outside of configs
				+ (numOfClassDuplicates + expectedDynamicDuplicates.length + StaticBlueprint.values().length + EnumBlueprint.values().length + 3) * 3; // what is provided by the combined configs 
		int actualPrinters = allPrinters.size();
		assertion(actualPrinters == expectedPrinters, "Expected " + expectedPrinters + " printer, but received " + actualPrinters);
		for (Printer p: allPrinters)
			p.print();
		
		int childSize = dynamicDuplicates.size();
		int parentSize = parentDuplicates.size();
		assertion(childSize == parentSize, "Should have " + childSize + " parent duplicates, but have " + parentSize);
		for (DynamicDuplicate d: dynamicDuplicates) {
			boolean found = false;
			DuplicationBlueprint details = d.getBlueprint();
			for (ParentDuplicate p: parentDuplicates) {
				if (p.isSameBlueprint(details)) {
					found = true;
					break;
				}
			}
			
			assertion(found, "Unable to find parent duplicate for " + d.getName());
		}

		int lib2Size = lib1DuplicatesFromLib2.size();
		assertion(parentSize == lib2Size, "Should have " + parentSize + " Lib1Lib2 duplicates, but have " + lib2Size);
		
		for (ParentDuplicate p: parentDuplicates) {
			boolean found = false;
			String pName = p.getBlueprint().getName();
			for (Lib1DuplicateBean b: lib1DuplicatesFromLib2) {
				if (b.getName().equals(pName)) {
					found = true;
					break;
				}
			}
			
			assertion(found, "Unable to find parent duplicate for " + pName);
		}
		
		int lib2StaticAppSize = lib2DuplicatesFromApp.size();
		int numEnums = StaticBlueprint.values().length;
		assertion(lib2StaticAppSize == numEnums, "Should have " + numEnums + " App duplicates duplicates, but have " + lib2StaticAppSize);
		

		assertion(originalBean instanceof Replace, "originalBean should be instance of Replace");
		assertion(originalBean2 instanceof Replace, "originalBean2 should be instance of Replace");
		assertion(originalBeanOption2 instanceof Replace, "originalBeanOption2 should be instance of Replace");
		assertion(originalOption1Bean instanceof ReplaceOption1, "originalBean should be instance of ReplaceOption1");
		assertion(originalNamed instanceof ReplaceNamed, "originalBean should be instance of ReplaceNamed");

		assertion(originalBean.getInt() == -321, "originalBean should have a value of -321, instead it is " + originalBean.getInt());
		assertion(originalBean2.getInt() == -321, "originalBean should have a value of -321, instead it is " + originalBean.getInt());
		assertion(originalBeanOption2.getInt() == -321, "originalBean should have a value of -321, instead it is " + originalBean.getInt());
		assertion(originalOption1Bean.getInt() == -432, "originalOption1Bean should have a value of -432, instead it is " + originalOption1Bean.getInt());
		assertion(originalNamed.getInt() == -543, "originalNamed should have a value of -543, instead it is " + originalNamed.getInt());

		assertion(originalBean != originalBean2, "originalBean is a singleton");
		assertion(originalBean != originalBeanOption2, "originalBean is a singleton");
		assertion(originalBean2 != originalBeanOption2, "originalBean2 is a singleton");
		assertion(originalOption1Bean != originalOption1Bean2, "originalOption1Bean is a singleton");
		assertion(originalNamed != originalNamed2, "originalNamed is a singleton");
		
		assertion(dataStruct.get().equals(expectedEnvironment), "Data should be " + expectedEnvironment + " but was " + dataStruct.get());
		
		assertion(dblValue == expectedDblValue, "Value should be " + expectedDblValue + " but was " + dblValue);

		if (expectedManualBean < 0)
			assertion(0 == ctx.count(new Descriptor<>(ManualBean.class)), "Manual bean should not be present");
		else {
			ManualBean manualBean = ctx.getBean(new Descriptor<>(ManualBean.class));
			assertion(new ManualBean(expectedManualBean).equals(manualBean), "Manual bean should have been " + expectedManualBean + " but was " + manualBean.getValue());
		}
		
		primitives.forEach((w) -> System.out.println(w));
		assertion(primitives.equals(Arrays.asList(PrimitiveType.values())), "Primitive array is not correct");
		
		assertion(exCtorBean != null, "exCtorBean should not be null");
		assertion(exCtorBean.isCreated(), "exCtorBean was not created");

		assertion(allIntWrappers.size() == 4, "There should be three " + IntWrapper.class.getSimpleName() + " instances");
		assertion(ctx.getAllBeans(new Descriptor<>(IntWrapperImpl.class)).size() == 0, "There should be no " + IntWrapperImpl.class.getSimpleName() + " instances");
		assertion(ctx.getAllBeans(new Descriptor<>(ReplaceIntWrapper.class)).size() == 0, "There should be no " + ReplaceIntWrapper.class.getSimpleName() + " instances");
		for (IntWrapper w: allIntWrappers) {
			if (w == replacedStandaloneWrapper) {
				assertion(w instanceof ReplaceIntWrapper, "Should be instance of " + ReplaceIntWrapper.class.getSimpleName() + " but was " + w.getClass().getSimpleName());
				int actual = w.getInt();
				assertion(actual == -123, "Unexpected value " + actual + " should be -123");
			} else if (w == replacedConfigWrapper) {
				assertion(w instanceof ReplaceIntWrapper, "Should be instance of " + ReplaceIntWrapper.class.getSimpleName() + " but was " + w.getClass().getSimpleName());
				int actual = w.getInt();
				assertion(actual == -321, "Unexpected value " + actual + " should be -123");
			} else {
				assertion(w instanceof IntWrapperImpl, "Should be instance of " + IntWrapperImpl.class.getSimpleName() + " but was " + w.getClass().getSimpleName());
				int actual = w.getInt();
				assertion(actual == 1 || actual == 2, "Unexpected value " + actual + " should have been either 1, 2");
			}
		}

		assertion(intList.size() == 7, " Integer list should have 7 entries but has " + intList.size() + " instead");
		assertion(intList.equals(Arrays.asList(1,2,3,4,5,6,7)), "Integer list entries are different");
		assertion(stringList.size() == 4, " String list should have 4 entries but has " + intList.size() + " instead");
		assertion(stringList.equals(Arrays.asList("a", "b", "c", "d")), "String list entries are different");

		intList.forEach(i -> System.out.println("INT LIST CONTAINS: " + i));
		stringList.forEach(s -> System.out.println("STRING LIST CONTAINS: " + s));
	}

	protected static void assertion(boolean value, String msg) {
		if (!value)
			throw new AssertionError(msg);
	}

	private void assertRunnableList(String annoucement, List<? extends Runnable> list, Runnable... runnables) {
		assertListContains(list, runnables);

		System.out.println("---------------------------- Running List " + annoucement + " ----------------------------");
		for (Runnable r : runnables)
			r.run();
		System.out.println("-------------------------------------------------------------------------------");
	}

	@SafeVarargs
	private <T> void assertListContains(List<? extends T> list, T... elements) {
		// Ensure that the list is correct
		assertion(list.size() == elements.length, "Expected " + elements.length + " elements, but recevied " + list.size());
		for (T e : elements)
			assertion(list.contains(e), "List " + list + " does not contain element " + e);
	}

	private void assertStringWrapperListContains(List<StringWrapper> actual, String... expected) {
		// Ensure that the list is correct
		assertion(actual.size() == expected.length, "Expected " + expected.length + " elements, but recevied " + actual.size());

		List<String> actualStrings = new ArrayList<String>();
		for (StringWrapper sw : actual)
			actualStrings.add(sw.getString());

		for (String e : expected)
			assertion(actualStrings.contains(e), "List " + actualStrings + " does not contain element " + e);
	}

	protected void assertDynamicDuplicatesInAll(Map<String, DynamicDuplicate> shouldBePresent) {
		assertion(dynamicDuplicates.size() == shouldBePresent.size(), "Different number of duplicates present, expected " + shouldBePresent.size() + " but was " + dynamicDuplicates.size());
		shouldBePresent.forEach((name, instance) -> {
			assertion(dynamicDuplicates.contains(instance), "Duplicate " + name + " is not present in dynamicDuplicates");
		});
	}

}
