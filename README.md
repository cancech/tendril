# Tendril
Tendril is an Annotation Processing based Dependency Injection, where the necessary code to perform dependency injection is generated at compile time and the dependency injection itself at runtime - no reflection required. The core usage is fairly straightforward, where a bean is defined in one location and then consumed in another. Three annotations are employed to indicate the "direction" of bean movement:

|Annotation|Description|
|---       | ---       |
|`@Bean`|Indicates the *production* of a bean, ergo a bean is created (outgoing).|
|`@Inject`|Indicates the *injection* of a bean, ergo a bean is consumed/retrieved (incoming).|
|`@InjectAll`|Indicates the *injection* of multiple related beans, ergo beans are consumed/retrieved (incoming).|

Since reflection is not employed, all beans (and any `Tendril` facing aspects) cannot be private. They must be at least `package private` in order for dependency injection to be successfully performed.

## Definitions
Some of these may be clear, some not, but for sake of completeness terms are defined here for easy reference.

|Term|Definition|
|--- | ---      |
|Bean|An object instance which is passed around via Dependency Injection|
|Configuration|A class which performs the steps to create and provide one or more Beans that are separate from the Configuration class itself.|
|Context|The logical bounds of the running application.|
|Dependency Injection|A mechanism for passing object from producers to consumers, such that consumers can obtain their necessary dependencies without needing to be concerned with where said dependencies come from.|
|Duplication|The process of creating multiple copies of a bean are created according to a specified blueprint. Injection of the bean copies takes place as per normal, with the exception that as many copies are produces as the blueprint indicates that should exist.|
|Qualifier|Annotation applied to a Bean to add descriptive information to the Bean, to make it possible/easier to find specific Beans (akin to metadata).|
|Quantifier|Annotation applied to a Bean to indicate how many copies of the Bean are to be produced.|
|Recipe|Generated class which contains the steps necessary to build a Bean within the `Tendril` framework.|
|Requirement|Condition which must be met for a bean to be allowed to be created.|
|Runner|Main entry point for a `Tendril` application.|
|Sibling|Beans which are created for the same blueprint instance are siblings of each other. There can be any number of beans created as siblings, so long as they are tied to the same blueprint instance.|

## Creating a Bean
The bean is the key ingredient in any Dependency Injection scheme, and `Tendril` is no different. There are two ways in which to define beans:
1. Within a class (where the class defines itself as a bean)
2. Within a `Configuration`

Regardless of which approach is taken to define a bean, each bean must be *quantified*, with the appropriate or desired *quantifier* annotation. It must be one of the following:

|Annotation|Description|
|---       | ---       |
|`@Singleton`| The first time the bean is accessed, an instance is created. This instance is then returned for each subsequent access of the bean. `Tendril` *guarantees* that only a single instance of the bean is ever employed as part of dependency injection. The class itself need not (in fact should not) actually follow the Singleton pattern.|
|`@Factory`| A new instance of the class is created for every access of the bean. `Tendril` *guarantees* that the same approach/mechanism is employed for the purpose of creating the bean, but a separate copy is always retrieved.|

### Bean Class
The simplest and most straight forward way to define a bean, is to make a class itself into a bean. Much like how a class defines the characteristics and capabilities of an enclosed *concept*, it can also define how it is to be used within `Tendril`. To do so, simply annotate the class with `@Bean` to indicate to `Tendril` that it is to be treated as a bean. Do not forget that the `quantifier` is still necessary. Thus a simple bean would look like the following:

```java
@Bean
@Singleton
public class MySingletonBean {
}

@Bean
@Factory
public class MyFactoryBean {
}
```

#### Multiple Constructors
If a bean class has a single valid constructor, this constructor is automatically employed for the purpose of creating the bean instance(s). In situations where a class has multiple valid constructors, it cannot be automatically inferred which constructor to use, thus one constructor must be identified using `@Inject` to indicate which constructor is to be used when initializing the bean.

```java
@Bean
@Singleton
public class MySingletonBean {
  @Inject
  public MySingletonBean() {
  }

  public MySingletonBean(int abc, String def) {
  }
}
```

Note that private constructors are not considered valid and are thus ignored for the purpose of bean processing. This means that if there is only a single non-private constructor, it need not be annotated with `@Inject`. Any private constructors are ignored, regardless of whether or not they are annotated with `@Inject`.

### Configuration Class
Where a bean class defines itself as a bean, a `Configuration` defines other beans. Meaning that the class itself is not a bean, but rather it provides one or more beans via methods. To create a `Configuration` a class is annotated with `@Configuration` and any methods which are to be used as the source of beans must be annotated with `@Bean`.

```java
@Configuration
public class MyConfiguration {

  @Bean
  @Singleton
  public Integer createIntegerBean() {
    return 123;
  }

  @Bean
  @Factory
  public String createStringBean() {
    return "abc123";
  }
}
```

In this situation, the `Configuration` will be initialized before any beans it provides are created, but only the beans it provides will be made available for injection (i.e.: the `Configuration` itself is transient, a means to an end). This allows for classes from outside of the codebase to be incorporated into `Tendril` dependency injection, as well as having multiple distinct copies of the same class to be made available as well. The same `Configuration` instance is employed for all beans it provides, meaning that the `Configuration` class itself is only initialized/created once.

### Placing Restriction on Bean Creation
Requirements can be placed on Beans and Configurations to limit under what circumstance they will be created. For example to use a different bean in a development, production, or test environment with little to no changes required in the code itself. Note that when a requirement is applied to a `Configuration` directly, it is implicitly applied to all Beans within (the whole `Configuration` will not be employed if the `Configuration` requirements are not met). Any requirements applied to Beans defined within a `Configuration` are in effect applied on top of the `Configuration` requirements. Multiples requirements can be applied, with them acting additively (i.e.: all specified requirements must be met).

#### Environments
The simplest manner in which to achieve this control is through the use of environments. Different environments can be specified when creating the `ApplicationContext` and requirements can be applied to Beans and Configurations to account for them. There are two way in which to specify environments:

1. Java `environments` property
2. In the code.

Regardless of the approach taken, the effect is cumulative; meaning that all environments specified via property and code are all applied. **_There is no means of removing an environment once it has been specified._**

##### Environments via Property
The `environments` property is loaded by the `Engine` on initialization to determine what the "preset" environments are. The value that is specified within the `environments` property will be applied to the runtime context, multiple environments can be supplied by using a comma separated list. For example to specify a single _envA_ the following JVM argument can be passed `-Denvironments=envA`. _envA_, _envB_, and _envC_ can be passed as follows `-Denvironments=envA,envB,envC`. The benefit of this approach is that it can be specified outside of the code/application, meaning that it is relatively easy for someone who cannot make any changes to the code have control over which environments are specified.

#### Environments via Code
The alternative approach, would be to specify the environments via code as part of `ApplicationContext` initialization. 

```java
ApplicationContext ctx = new ApplicationContext();
ctx.addEnvironments("a", "b", "c");
ctx.start();
```

Any number of environments can be provided to `addEnvironments()` and it can be called multiple times. The only limitation is, that it can only be called prior to starting the context. Once the context has been started, the environments become fixed and no more can be added.

#### Restricting Bean Creation via Environments
To apply requirements on a Bean or Configuration the following annotations can be used:

* `@RequiresEnv` and specify one (or more) environments, *all of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresOneOfEnv` and specify one (or more) environments, *at least one of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresNotEnv` and specifying one (or more) environments, *none of which* can be applied for the Bean of Configuration to be allowed to be created.

```java
@Bean
@Singleton
@RequiresEnv("a")
@RequiresNotEnv("b")
public class MyEnvANotBClass {
}

@Bean
@Singleton
@RequiresEnv({"b", "c"})
@RequiresNotEnv({"d", "e"})
@RequiredOneOfEnv({"f", "g", "h"})
public class MyEnvBandCNotDandEClass {
}
```

`@RequiresEnv` can be view as an `and`, where *all* of the environments must be present. `@RequiredOneOfEnv` can be viewed as an `or`, where *at least one* of the environments must be present. `@RequiredNotEnv` can be viewed as a `not`, where *none* of the listed environments can be present.

## Consuming a Bean
In essence, the act of creating Beans is also the act of consuming them. Bean consumption is performed as part of Bean creation, where a Bean consumes its dependencies before it itself is provided onward to whomever depends on it. Thus, consuming a Bean is the act of defining what other Bean a given Bean depends on. This is done via the `@Inject` annotation, which can be applied on:
* Constructors - the parameters the constructor takes are treated as bean dependencies and provided when called. Note: only one constructor can be injected.
* Instance Fields - instance fields annotated with `@Inject` or `@InjectAll` are automatically fulfilled by dependency injection. Note: any number of instance fields can be injected.
* Methods - any method annotated with `@Inject` is automatically called (once) as part of dependency injection, with all parameters it contains treated as dependencies that are to be provided. Note: any number of methods (provided they are not constructors) can be injected.

This applies to both `Bean Classes` as well as `Configurations`.

```java
@Bean
@Singleton
public class MyBeanClass {

  @Inject
  String stringDependency;

  MyBeanClass(Integer intDependency) {
  }

  @Inject
  void injectionMethod(Double dblDependency, Long longDependency) {
  }

}
```

`Configuration` classes have the extra Bean injection potential via the Bean creation method. Any parameters that the `@Bean` method takes are treated as Beans dependencies to be injected

```
@Configuration
public class MyConfiguration {

  @Inject
  String stringDependency;

  MyConfiguration(Integer intDependency) {
  }

  @Inject
  void injectionMethod(Double dblDependency, Long longDependency) {
  }

  @Bean
  @Factory
  MyClass buildBean(MyOtherClass otherBean, ExampleBean exampleBean) {
  }
}
```

#### Injecting Across Configuration Beans
If there are Beans defined within a single `Configuration` which depend on each other, they cannot call each other directly. Doing so will cause the bypass the `Tendril` mechanism, with potentially unintended side-effects. If the Bean is question is a `@Factory` this side-effect may be ultimately inconsequential, however for `@Singleton` this will result in an additional instance of the Bean being created.

```java
@Configuration
public class MyConfiguration {

	@Bean
	@Singleton
	MyClass createMyClass() {
		return new MyClass();
	}
  
	@Bean
	@Singleton
	MyOtherClass createMyOtherClass() {
		// Calling createMyClass() directly results in an instance of MyClass being created
		// outside of the Tendril dependency injection mechanism. Thus while this will properly
		// compile and run, a separate instance of MyClass will be employed which will have
		// runtime consequences, depending on the nature and capabilities of MyClass.
		return new MyOtherClass(createMyClass());
	}
}
```

In this situation, it is necessary to rely on proper dependency injection methods.

```java
@Configuration
public class MyConfiguration {

	@Bean
	@Singleton
	MyClass createMyClass() {
		return new MyClass();
	}

	@Bean
	@Singleton
	MyOtherClass createMyOtherClass(MyClass myClass) {
		// MyClass is properly injected and provided via Tendril.
		// The appropriate MyClass singleton instance is employed
		return new MyOtherClass(myClass);
	}
}
```

Some things to note:
* as mentioned at the start, no injected element can be `private`
* per above, the `@Inject` annotation on a constructor is only necessary if there are multiple valid constructors on a class. It is still recommended to always apply the annotation regardless to make it explicitly clear (both to `Tendril` as well as anyone looking at the code in the future).

#### Injecting Multiple Related Beans
`@Inject` is the *default* injector, with the limitation that it must resolve to exactly one bean. An error will be thrown if either no matching bean is available, or if multiple beans match the required description. The `@InjectAll` can be used to avoid this limitation. The expectation and behavior of `@InjectAll` is as follows:
* `@InjectAll` can only be applied to fields or parameters
* The field must be a `List` and the *type parameter* must reflect the desired *type* of the bean (i.e.: `@InjectAll List<Runnable>` will inject `Runnable` beans)
* The field can be qualified in the same manner as `@Inject`, with the same rules/options supported
* All beans in the application context which match the supplied qualifiers will be retrieved
* If no bean matches the supplied qualifiers, and empty list will be retrieved (no error is thrown if no matching beans are available)
* When applied to a parameter, then method or constructor it belongs to must be annotated with `@Inject` (either explicitly or implicitly)

Accordingly, `Tendril` provides zero or more matching beans to the `@InjectAll` field, leaving it up to the client code to perform the necessary actions/operations as necessary upon them.

```java
@Bean
@Singleton
public class MyBeanClass {

	@InjectAll
	List<Runnable> runnables;
  
	@Inject
	MyBeanClass(@InjectAll List<String> strings) {
	}
  
	@Inject
	void methodInjector(MyClass bean1, MyOtherClass bean2, @InjectAll List<Object> allBeans) {
	}
}
```

## Qualify Beans
As the number of beans within an application grows, it becomes important to provide means of qualifying or describing beans. This is crucial once multiple Beans of the same type (either directly through a `Configuration` or indirectly through their inheritance hierarchy) are to be employed. This is where `qualifiers` come into the picture, acting as descriptors (i.e.: metadata) for each Bean, allowing for fine-grained differentiation of one Bean from another. The most basic differentiator is the type (i.e.: class) of the Bean, dictated by either:
* The class for a `Class Bean`
* The return type of a `Configuration Bean`
This type, or something from its inheritance hierarchy, must be used for the purpose of retrieving the Bean (i.e.: must be able to cast the Bean to the desired type). Beyond this most basic `qualifier` additional options are available through which to provide additional information to distinguish one Bean from another.

Note that none of these `qualifiers` (including Bean Type) are unique, as each can be applied to any number of Beans. Uniqueness is not guaranteed by any individual `qualifier` and it is up to the user to either enforce uniqueness manually when building beans if such is required, or to employ a combination of `qualifiers` which together uniquely identify a specific Bean.

### @Named
Using the `@Named` annotation allows for applying a `String` name to a Bean. There is no restriction placed on what can be contained within the `String`.

```java
@Bean
@Singleton
@Named("qwerty")
public class MyClass {
}

@Bean
@Singleton
public class MyOtherClass {

	@Inject
	@Named("qwerty")
	MyClass myClass;
}
```

This is the simplest manner in which to qualify a bean, however due to no compile or even runtime validation of the supplied name, then there is a very high probability of errors that will not be seen/noticed until runtime. Some of this can be mitigated through the use of `static final String` variables, however this does not outright resolve the issue.

### Enum ID
It is possible to employ an `Enum` as an ID on a bean using a two-step process. First, the `Enum` itself must be annotated with `@BeanIdEnum` which tells `Tendril` to generate an annotation `@<Enum>Id` which can then be applied to beans. This generated annotation takes a value from the `@BeanIdEnum` annotated `Enum`, which is then applied as a `qualifier` to the Bean.

```java
@BeanIdEnum
public enum MyEnum {
	VALUE1, VALUE2;
}

@Bean
@Singleton
@MyEnumId(MyEnum.VALUE1)
public class MyClass {
}

@Bean
@Singleton
public class MyOtherClass {

	@Inject
	@MyEnumId(MyEnum.VALUE1)
	MyClass myClassBean;
}
```
The generated `@<Enum>Id` is co-located in the same `package` as the `@BeanIdEnum` annotated enumeration.

When used, the `@<Enum>Id` qualifier ultimately serves the exact same function as `@Named`, just with a touch of compiler validation and verification added into the mix to lessen the risk of misnamed beans. This cannot remove the issues associated with employing the *wrong* value, but it will decrease (if not outright remove) risks of typos. For this reason it is recommended to employ `@<Enum>Id` is place of `@Named` whenever possible.

### Qualifier Annotations
Custom qualifier annotations can be creating, by simply defining an annotation and annotating it with the `@Qualifier` annotation. This marks that annotation as one which can be used as a qualifier and it will be processed as such by `Tendril`. The only other caveats being:
* it must have a `@Retention` of at least `RUNTIME` to ensure that it can be used outside of the defining library
* the `@Target` should include `TYPE`, `METHOD`, `FIELD`, and `PARAMETER` at a minimum, so that it can be used in all areas where beans are either produced or injected.

An example qualifier can be defined as follows:

```java
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MyQualifier {
}
```
The annotation does not require any attributes, in fact if any are defined they will be ignored by `Tendril`. Once defined, the qualified can be applied to beans both when defined and injected.

```java
@Bean
@Singleton
@MyQualifier
public class MyClass {
}

@Bean
@Singleton
public class MyOtherClass {

	@Inject
	@MyQualifier
	MyClass myClassBean;
}
```

At a high level, the `qualifier annotation` can act as a `Name` or `Enum ID`, with one major distinction. Any number of *distinct* qualifiers can be applied to a single bean, whereas only a single `Name` or `Enum ID` (for a given `Enum`) can be applied. In that respect the `qualifier annotation` can be viewed as a more flexible option.

#### Generated Qualifiers
Since every `qualifier annotation` will look, behave, and be implemented in the exact same manner, with the only difference being the class/annotation name, the `qualifier annotation` essentially becomes a bit of boiler plate code. To alleviate some of the drudgery involved with the definition and maintenance of these qualifiers, it is possible to use the `@QualifierEnum` to generate the desired qualifiers. In this manner the various qualifiers are defined in an `enum`, such that each value represents a single qualifier. No parameters/values are necessary, and the `enum` value name is used verbatim to generate a corresponding `qualifier annotation`.

```java
@QualifierEnum
public enum MyQualifiers {
	Value1, Value2;
}
```

will generate

```java
@GeneratedQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Qualifier
public @interface Value1 {

}

@GeneratedQualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Qualifier
public @interface Value2 {

}
```

which can then be employed as needed to qualify beans in your application.

```java
@Bean
@Factory
@Value1
@Value2
public class MyClass {

}
```

The name of the `enum` is irrelevant, as it is not used as part of the generation process. Only the `enum value` is employed. This also alleviates the restriction from `Enum ID` as each generated anotation is distinct any number of them can be applied to a single bean.\

## Prioritizing Beans
In some situations it may be necessary to *prioritize* which beans are used by applying a *type* to them. These types can be either:
* `Primary` - high priority
* `Basic`- normal priority
* `Fallback` - low priority, only to be used when no other bean is available

This is done when defining a bean (whether as a class or method) by annotating it with `@Primary` to make it a primary bean, or `@Fallback` to make it a fallback bean. If neither is applied it will be a default `Basic` bean. How this comes into effect depends on how the bean is retrieved.

### `@Inject` Bean Priority
When injecting a single bean via `@Inject`, it will be pulled from the highest priority level that is available from the beans which match the injection, regardless of how many beans may exist in the lower priority levels. For example: the single matching `@Primary` bean will be returned regardless of how many `Basic` or `@Fallback` matches are present. If there is no `@Primary` match, then the single `Basic` bean will be returned, regardless of how many `@Fallback` ones are present. If there are no `@Primary` or `Basic` beans, then there must be a single `@Fallback` bean in the results. There must be exactly one match in the highest available level when injecting an `@Inject` bean, otherwise an exception will be thrown.

In this situation, the `@Primary` can be seen as a means of *elevating* one bean over others. For example, if `BeanB` extends `BeanA` then `@Priority` can be employed to prioritize the parent class when making an injection.

```java
@Bean
@Singleton
@Priority
public class BeanA {
}

@Bean
@Singleton
public class BeanB extends BeanA {
}

@Bean
@Singleton
@Fallback
@MyQualifier
public class BeanC extends BeanA {
}

@Inject
BeanA beanA;

@InjectAll
@MyQualifier
BeanA myQualifierBeanA;
```

In the code snippet above, by making `BeanA` higher priority than `BeanB`, injecting `BeanA` will favor the class `BeanA` over `BeanB` and `BeanC`. Otherwise, `@Inject BeanA` will not know which instance to pull in as `BeanA`, `BeanB`, and `BeanC` are all viable options for the injection without any other qualifiers being present. On the flip side `@Fallback` acts as the name implies, a fallback or fail safe to ensure that a valid bean is provided when there are no other options available. For example, if an important bean is expected to be provided (such as a database or network connection/handler) then a `@Fallback` bean can be used to ensure that some kind of sane default is provided, rather than risking a crash due to an important component not being available. Accordingly in the code snippet above, `myQualifierBeanA` will receive
`BeanC` as there is no other `BeanA` castable bean with the `@MyQualifier` applied.

### `@InjectAll` Bean Priority
When injecting multiple beans via `@InjectAll`, then all `@Primary` and `Basic` beans matching the injection will be provided as there is no limitation or stipulation on the number of possible matches. `@Fallback` beans will only be provided if there are no higher priority beans available. In this situation, there is no functional difference between the `@Primary` and `Basic` beans (that distinction is only used by `@Inject`). However, `@Fallback` maintains its fail safe role, as again no `@Fallback` beans will be provided if there are any other beans available.

```java
@Bean
@Singleton
@Priority
public class BeanA {
}

@Bean
@Singleton
public class BeanB extends BeanA {
}

@Bean
@Singleton
@Fallback
@MyQualifier
public class BeanC extends BeanA {
}

@InjectAll
List<BeanA> allBeanA;

@InjectAll
@MyQualifier
List<BeanA> allMyQualifierBeanA;
```

In the code snippet above `allBeanA` will contain the `BeanA` and `BeanB` beans, as both can be cast to `BeanA`. `BeanC` will not be included as there are `@Primary` (BeanA) and `Basic` (BeanB) beans that meet the injection criteria. On the other hand, `allMyQualifierBeanA` will include only `BeanC`, as that is the only bean which can be cast to `BeanA` and includes the qualifier `@MyQualifier`.

## Duplicating Beans
It is possible to create multiple copies of "the same" bean, such that the same definition mechanism is employed for all. This takes a *cookie cutter* approach, where a blueprint is employed to define how many copies are to be produced and what the distinctions between them are and `Tendril` will automatically produce and provide the appropriate beans. The blueprint is `Enum` driven, any enum can be used it just needs to be annotated with `@Blueprint` and each value within the blueprint enum will be then to define a specific copy. Any desired characteristics which are unique to a given copy can be included in the enum, thus allowing for distinct variations between different copies with the only limitation being dictated by the enum construct itself.

```java
@Blueprint
public enum MyDuplicates {
	COPY_1("copy1"),
	COPY_2("copy2"),
	COPY_3("copy3");
	
	private final String name;
	
	private MyDuplicate(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
```

From this two things will be generated: a blueprint annotation and qualifier for each duplicate (enum value). The blueprint annotation will take the name of the enum and append `Blueprint` to it (thus `@MyDuplicatesBlueprint` in this example) and can be used instead of `@Bean` to define a bean which is to be duplicated. Note that the `quantifier` must still be included as per a regular bean.

```java
@MyDuplicatesBlueprint
@Singleton
public class MyBean {

}
```

This will trigger the creation of as many unique beans as there are values in the enum. Any qualifiers and restrictions can be placed onto the blueprint bean as per any other bean and these will be applied to all copies. Unlike regular beans, the qualifier generated for each enum value will automatically be applied to the bean created for the specific duplicate. Dependencies that are outside of the duplicate can be injected as-per normal using the traditional mechanisms.

```java
@MyDuplicatesBlueprint
@Singleton
@CustomQualifier
@RequiresEnv("env")
public class MyBean {

	@Inject
	@Named("abc123")
	OtherBean otherBean;

}
```

Duplicate beans can be injected as any other bean, with the distinction that there will be however many desired duplicates of the bean. As the manually applied qualifiers (including name and Enum ID) will be applied to all copies, the only way in which to uniquely distinguish one copy from the others is using the generated duplicate qualifier that is automatically applied.

```java
@Bean
@Singleton
public class MyConsumer {
	
	@Inject
	@COPY_1
	MyBean bean1;
	
	@Inject
	@COPY_2
	MyBean bean2;
	
	@Inject
	@COPY_3
	MyBean bean3;
}
```

### Injecting the Blueprint
As the copies are all identical with the exception of the blueprint enum value used to generate it, any unique values for the copy must be provided via the enum and these values need to be made available to the bean. This can be done by *injecting* the enum and annotating it with `@Sibling`. This can be done via the constructor, instance field, or method.

```java
@MyDuplicatesBlueprint
@Singleton
public class MyBean {

	@Inject
	@Sibling
	MyDuplicates blueprint;
	
	@Inject
	MyBean(@Sibling MyDuplicates blueprint) {
	}
	
	@Inject
	void doSomething(@Sibling MyDuplicates blueprint) {
	}

}
```

This will inject the specific enum value that was used to produce the copy, and it can then be used within the bean in whatever manner necessary to configure or otherwise handle itself appropriately.

### Injecting Sibling Beans
If an explicit instance of a duplicate bean is required, then the qualifier of the duplicate can be used directly (whether within a duplicate or outside of it).

```java
@MyDuplicatesBlueprint
@Singleton
public class BeanA {

}

@MyDuplicatesBlueprint
@Singleton
public class BeanB {

	@Inject
	@COPY_1 // This will always inject the COPY_1 instance of BeanA
	BeanA beanA;
}
```

However, when it becomes necessary to inject other beans which belong to the same sibling this will fall short as this is hardcoded to a given instance. If the duplicate belong to the same sibling is required, then the `@Sibling` annotation can be employed. This will provide the instance of the bean which belongs to the same copy.

```java
@MyDuplicatesBlueprint
@Singleton
public class BeanA {

}

@MyDuplicatesBlueprint
@Singleton
public class BeanB {

	@Inject
	@Sibling
	BeanA beanA;
}
```

Thus, now when `BeanB` is created for `COPY_1` then `COPY_1` instance of `BeanA` will be provided (and so on for `COPY_2`, `COPY_3`, and so on).

Note that this mechanism relies on simply *replacing* the `@Sibling` with the appropriate qualifier for the sibling, meaning that appropriate differentiation techniques must still be employed if there are multiple copies of the bean within the sibling.

```java
@MyDuplicatesBlueprint
@Singleton
@Primary // Prioritize BeanA over ChildA for injection
public class BeanA {

}

@MyDuplicatesBlueprint
@Singleton
public class ChildA extends BeanA {

}

@MyDuplicatesBlueprint
@Singleton
public class BeanB {

	@Inject
	@Sibling
	BeanA beanA;
	// Without making BeanA @Primary, Tendril will report an error during injection
	// as both BeanA and ChildA will be valid injections
}
```

## Transferable Annotations
Certain annotations (namely Qualifiers and Requirements) are transferable, which in this context means that they can be applied anywhere in the annotation hierarchy and have an effect on the element (namely Bean and Configuration) as if they were applied to the element directly. Thus, common combinations can be placed into a shared/reusable annotation which is then applied to the appropriate Beans or Configurations, avoiding the need to "redefine" the combination in multiple places. Note that the reusable annotation must include `@Retention(RetentionPolicy.RUNTIME)` and have the appropriate `@Target` configured for its various use cases. This allows for defining custom annotation, and refactoring these transferable annotations away from concrete Beans or Configurations.

```java
@QualifierEnum
public enum MyQualifiers {
	ExampleElement;
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@RequiresNotEnv("TEST")
@ExampleElement
public @interface ProductionElement {
}

@ProductionElement // provides the transferable annotations @RequiresNotEnv("TEST") and @ExampleElement
public class MyBean {
}
```

## Bean Lifecycle
There is a rigid lifecycle that Beans adhere to within the `Tendril` ecosystem, starting from initialization all the way to destruction. No Bean is provided until all of its dependencies have been completely satisfied, ergo `Tendril` guarantees that any Bean which is injected is *complete* (as far as `Tendril` is concerned). The initialization order of a Bean is as follows:
1. Constructor is called
2. Fields/Methods are populated/called
3. `@PostConstruct` methods are called
4. Bean is provided:
  * for a `Bean Class` this means it is then provided as-is
  * for a `Configuration` this means that the appropriate method for the desired Bean is called and then provided

Note: it is guaranteed that fields and methods are injected after the constructor is called to create the Bean instance and before `@PostConstruct` methods are called, however there is no guarantee as to the order within. Meaning that there is no guarantee in terms of the order in which fields/methods are called/initialized. Thus it is important to ensure that each is implemented assuming it is called first (i.e.: nothing else has been called/initialized yet). If order is important, this should be moved either to a `@PostConstruct` to ensure that it is called after all injection has been completed, or in a single method where all dependencies are required are injected as parameters.

### @PostConstruct
This annotation can be applied to any non-`private` method, any number of `@PostConstruct` methods can be present. These methods cannot take any parameters and are expected to be `void` (any returned value is ignored). They will be automatically called (once) after all Bean dependencies have been satisfied. While it is guaranteed that it will be called after all dependencies are satisfied and before the Bean is injected elsewhere, there is no guarantee as to the order in which `@PostConstruct` methods will be called. Consequently, no `@PostConstruct` should depend on the actions/operations of another `@PostConstruct`. If such a dependency does exist, this should be instead be implemented such that it is triggered by a single `@PostConstruct`:
* condense all actions into a single `@PostConstruct` method
* remove `@PostConstruct` from affected methods and provide a single `@PostConstruct` which calls the affected methods in the required order.

```java
@Bean
@Singleton
public class MyBeanClass {

  @Inject
  String stringDependency;

  MyBeanClass(Integer intDependency) {
  }

  @PostConstruct
  void doSomething() {
  }

  @PostConstruct
  void doSomethingElse() {
  }

  @Inject
  void injectionMethod(Double dblDependency, Long longDependency) {
  }

}
```

### Bean Destruction
There is no explicit mechanism for destroying a Bean, with appropriate Java garbage collection mechanisms employed for each instance depending on the `quantifier` employed. For the various options, the following approaches are taken.

|Annotation|Destruction|
|---       | ---       |
|`@Singleton`| The single instance is maintained for the entire application lifecycle - this instance is created the first time it is accessed and kept until the application shuts down. There is no mechanism through which to trigger its destruction prematurely.|
|`@Factory`| Since a new instance is created for access to the Bean, the lifecycle of each instance it not controlled by `Tendril`. Rather it is up to the consumer to control when/if the instance is destroyed (i.e.: it is only destroyed once it is no longer referenced).|

## Creating an Application
The ability to pass Beans is crucial, however this in of itself is insufficient for the purpose of driving an application. In order to be able to create a `Tendril` application, two additional pieces are required.

### Define a Runner
Much like `public static void main(String[] args)` is the entry point into an application, a `Runner` must be defined as the entry point into a `Tendril` application. The `Runner` is a class which must fulfill two exact criteria:
1. It must implement the `TendrilRunner` interface
2. It must be annotated with `@Runner`
3. If more than one `Runner` is to be made available, the appropriate `Requirements` must be applied to each.

It is from this `Runner` that the `Tendril` application execution/processing starts, and it is this `Runner` which triggers the start of dependency injection with the application. Conceptually the `Runner` is equivalent to a `Bean Class` (everything that has been written about `Bean Classes` applies to `Runners` as well) other than the annotations which are employed. In practice, the only difference from the `Bean Class` is that upon completing the assembly of the `Runner`, rather than injecting it as a dependency, the `run()` method is called. Once `run()` returns, the application as a whole is considered "done".

```java
@Runner
public class Main implements TendrilRunner {

  @Inject
  String stringBean

  @Inject
  Main(MyClass myClassBean) {
  }

  @PostConstruct
  void performSetup() {
  }

  @Override
  public void run() {
    // Run your application
  }
}
```

The application must have exactly **one** `Runner` available at runtime, with an exception thrown if more or less are available at application start. If multiple runners are to be provided in the code, then it is necessary to supply the appropriate `Requirements` to each, such that only a single `Runner` is available to the application. For example, one `Runner` can be created for use in a production environment and another in a test environment, with the appropriate requirements (i.e.: `@RequiresEnv` or `@RequiresNotEnv`) supplied to allow for their down selection.

### Create the Application Context
The dependency injection takes place within a `Context`, which must be created and started. This is done via `ApplicationContext`, which is to be created and started in the global application `main()` or equivalent.

```java
public static void main(String[] args) {
  ApplicationContext ctx = new ApplicationContext();
  ctx.start();
}
```
`ApplicationContext::start()` will find and `run()` the `Runner` that is defined, triggering assembly and execution of the `Tendril` application.

## META-INF
In support of `Tendril` functionality, the build will generate a number of supporting files in the `META-INF/tendril` directory. These files are vital to the runtime operations of the application and must be preserved. If a tool such as `shadow` is used to create a *Fat* or *Uber* jar, care must be taken to ensure that the `META-INF/tendril` files are combined or merged and not overridden. The loss of data which would ensure will directly result in loss of bean (meta) data and failure for the resulting jar/application to work properly. The following `META-INF/tendril` files are produced

|File Name| Description|
|---    | ---       |
|`registry`|List of all `Recipes` that are present for the purpose of producing beans. This file is loaded by the `Engine` to determine what all `Recipes` (and thus beans) are available for injection. Looked at a little differently, this is a list of all `@Registry` annotated classes in the application.|
|`runner`|List of `@Runner` annotated classes, ergo potential entry points into the `Application Context`.|

## Supporting Libraries
`Tendril` is divided into a number of supporting libraries, each with a specific role to fulfill in order to achieve the functionality and capability defined here-in. All of these libraries are available for use outside of a `Tendril` application as described here and can be used to extend/expand the capabilities provided by `Tendril` or for other unrelated purposes. Refer to each library for additional information for how it works and how it operates. This can also be seen as a "peak behind the hood" of how `Tendril` works.

|Library|Description|
|---    | ---       |
|[tendril-annotation-processor](./tendril-annotation-processor)|Core functionality driving the annotation processing capabilities|
|[tendril-codegen](./tendril-codegen)|Library facilitating code generation via factories and builders, to simplify the process of generating code.|
|[tendril-di](./tendril-di)|The user facing Dependency Injection capability, with the annotation processor to generated the necessary code for `Tendril` to work.|
|[tendril-test](./tendril-test)|Automated/Unit test library, to facilitate and simplify the creation of JUnit tests.|
|[tendril-test-app](./tendril-test-app)|A sample application which employs `Tendril`, primarily for the purpose of providing a test bed in which the various capabilities can be tested and verified.|
