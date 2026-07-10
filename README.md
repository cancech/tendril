# Tendril
Tendril is an Annotation Processing based Dependency Injection, where the necessary code to perform dependency injection is generated at compile time and the dependency injection itself at runtime - no reflection required. The core usage is fairly straightforward, where a bean is defined in one location and then consumed in another. Three annotations are employed to indicate the "direction" of bean movement:

|Annotation|Description|
|---       | ---       |
|`@Bean`|Indicates the *production* of a bean, ergo a bean is created (outgoing).|
|`@Inject`|Indicates the *injection* of a bean, ergo a bean is consumed/retrieved (incoming).|
|`@InjectAll`|Indicates the *injection* of multiple related beans, ergo beans are consumed/retrieved (incoming).|

Since reflection is not employed, all defined beans cannot be private. They must be at least `package private` in order for the bean to be properly registered. Elements which are to be injected within the bean can be of any visibility, however reflection is employed to perform the injection of any element which is not directly accessible from the generated recipe. As such, it is recommended to be careful of this if the reflection overhead is to be avoided.

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

#### Override Bean Class Type
By default, the type of the bean is determined by the annotated type. For a bean class, this means that the type of the class is applied to the recipe and that is how the bean is "advertised" for dependency injection. However it is possible to override this by specify the desired class within the `@Bean` injection. The main purpose for this would be to hide the exact implementation employed and for the bean to only be accessed through the desired interface (or other parent).

```java
// The bean will only be accessible as a Parent
@Bean(Parent.class)
@Singleton
public class Child extends Parent {
}

// The bean will only be accessible as an IFace
@Bean(IFace.class)
@Factory
public class MyBean implements IFace {
}
```

Note that the override type must be an ancestor of the actual bean object, meaning that not just _anything_ can be used.

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

#### Override Bean Method Type
Much as the case with a bean class, when the bean is defined via a method it can be overridden by way of the `@Bean` annotation. The only difference in this case is that by default the return type of the method is applied as the advertised as the bean type, but again this can be overridden by specifying the desired type in the `@Bean` annotation.

```java
@Configuration
public class MyConfiguration {

  @Bean(Object.class)
  @Singleton
  public Integer createIntegerBean() {
    return 123;
  }
}
```

The limitation and implications are the same as when employed with a bean class.

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
ApplicationContextBuilder builder = new ApplicationContextBuilder();
builder.addEnvironments("a", "b", "c");
ApplicationContext ctx = builder.build();
ctx.start();
```

Any number of environments can be provided to `addEnvironments()` and it can be called multiple times. The only limitation is, that it can only be called prior to starting the context. Once the context has been started, the environments become fixed and no more can be added.

#### Restricting Bean Creation via Environments
To apply requirements on a Bean or Configuration the following annotations can be used:

* `@RequiresEnv` and specify one (or more) environments, *all of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresProp` and specify one (or more) system properties, *all of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresOneOfEnv` and specify one (or more) environments, *at least one of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresOneOfProp` and specify one (or more) system properties, *at least one of which* must be applied for the Bean or Configuration to be allowed to be created.
* `@RequiresNotEnv` and specifying one (or more) environments, *none of which* can be applied for the Bean of Configuration to be allowed to be created.
* `@RequiresNotProp` and specifying one (or more) system properties, *none of which* can be applied for the Bean of Configuration to be allowed to be created.

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

`@Requires<type>` can be view as an `and`, where *all* of the value types must be present. `@RequiredOneOf<type>` can be viewed as an `or`, where *at least one* of the value types must be present. `@RequiredNot<type>` can be viewed as a `not`, where *none* of the listed value types can be present.

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
* as mentioned at the start, no defined bean method can be `private`
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
It is possible to create multiple copies of "the same" bean, such that the same definition mechanism is employed for all. This takes a *cookie cutter* approach, where a *blueprint* is employed to define how many copies are to be produced and what the distinctions between them are and `Tendril` will automatically produce and provide the appropriate beans. As such, the duplication process is effectively divided into three parts:

1. Blueprint - defines what are the details of a given duplication process (i.e.: how does one duplicate differ from another)
2. Bean - trigger the creation of duplicates of a single "bean", such that one instance is created for every blueprint that is provided
3. Bean Passing - pass the created duplicates either amongst themselves or to others outside of the duplication process.

### Define Blueprints
To define a blueprint simply implement the `Blueprint` interface 

```java
// Minimal example
public class MyBlueprint implements Blueprint {

	private final String name;
	
	public MyBlueprint(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
```

or extend from the default implementation `BasicBlueprint`. Any other/additional details can be incorporated and it will be up to the client code to perform any necessary validation and processing of the additional data.

```java
// Minimal example
public class MyBlueprint extends BasicBlueprint {

	private final int intVal;
	private final MyData data;

	public MyBlueprint(String name, int intVal, MyData data) {
		super(name);
		this.intVal = intVal;
		this.data = data;
	}
	
	public int getIntValue() {
		return intVal;
	}
	
	public MyData getData() {
		return data;
	}
}
```

Only the `name` is mandatory, as that is used internally within `Tendril` and when copies are created each distinct copy of a bean will be `@Named` with this name. Once the blueprint to use is defined, it must be registered with the `ApplicationContextBuilder` to ensure that the application context is aware of what instances are to be created.

```java
ApplicationContextBuilder builder = new ApplicationContextBuilder();
builder.addBlueprint(new MyBlueprint("abc123", 123, new MyData("a", "b", "c"));
builder.addBlueprint(new MyBlueprint("def456", 456, new MyData("d", "e", "f"));
builder.addBlueprint(new MyBlueprint("ghi789", 789, new MyData("g", "h", "i"));
ApplicationContext ctx = builder.build();
ctx.start();
```

As mentioned, the `name` specified in the blueprint is applied in the same manner as if `@Named(BlueprintDriver::getName())` were called, and it can then also be used to retrieve unique copies for a given blueprint. To this end, it is necessary that the name be unique for every copy for a given blueprint, though the name can be reused across different types of blueprints.

```java
ApplicationContextBuilder builder = new ApplicationContextBuilder();
// Unique to MyDuplicates - this is fine
builder.addDynamicDuplicate(new MyDuplicates("a");
builder.addDynamicDuplicate(new MyDuplicates("b");
builder.addDynamicDuplicate(new MyDuplicates("c");

// This will fail as a copy names "a" already exists
builder.addDynamicDuplicate(new MyDuplicates("a");

// This is fine as there is no "a" in MyOtherDuplicates as of yet
builder.addDynamicDuplicate(new MyOtherDuplicates("a");

ApplicationContext ctx = builder.build();
ctx.start();
```

### Duplicate Beans
Duplicating beans largely follows the same patterns as defining regular beans, except that the `@Duplicate` annotation is employed instead of `@Bean`. `@Duplicate` requires a `Blueprint` to be specified and ultimately tells `Tendril` _"Create as many copies of this bean as exist copies of the specified blueprint"_

```java
@Duplicate(MyBlueprint.class)
@Singleton
public class MyDuplicatedBean {

	// ...

}
```

As such, when defining the bean it is not explicitly known how many copies (if any) of it will be created. Merely, that for every `Blueprint` of the type indicated (`MyBlueprint` in the example above) one copy of this bean will be created. When combined with `@Singleton`, each copy of the bean will be a singleton (ergo, only one copy of the duplicate will exist and will be passed around), whereas `@Factory` will create a new copy of the given duplicate each time it is accessed. In this sense the _quantifier_ works in exactly the same manner as when applied to `@Bean`, albeit there are potentially multiple distinct versions available.

The same applies whether duplicating a class bean (as per above) or as a `Configuration` bean, and within a `Configuration` duplicate and non-duplicate beans can coexist within the same file.

```java
@Configuration
public class MyConfiguration {

	@Bean
	@Singleton
	public MyBean createMyBean() {
		return new MyBean();
	}

	@Duplicate(MyBlueprint.class)
	@Factory
	public MyDuplicate createDuplicate() {
		return new MyDuplicate();
	}
}
```

Note that while the `name` of the duplicate is automatically applied, any other _qualifiers_ that are applied to the bean will be applied to _all_ duplicate copies of it. As such additional _qualifiers_ can be used to differentiate one "group" of duplicates from another, but not one unique copy from within a group. The exception to this being of course `@Named`, the `@Named` qualifier cannot be applied when `@Duplicate` is employed as it is used automatically.

### Injecting Duplicate Beans
There are in essence two different approaches to duplicate bean injection: external and internal to the duplication mechanism.

#### External Injection
This is the "standard" injection, where any arbitrary bean can inject any other arbitrary bean. The only distinction being, that if the bean being injected is a duplicate, there is no way to know at compile time how many copies of it will exist. As such, it should always be approached with the understanding that _any number_ of copies of the bean may exist, meaning that a simple `@Inject` will most likely fail due to more than one instance being available. As such, `@InjectAll` should be employed instead. Given that each instance is given the `name` from the `Blueprint` in a more controlled environment it is possible to inject the concrete instance by incorporating the desired name.

```java
@Duplicate(MyBlueprint.class)
@Singleton
public class MyDuplicatedBean {

	// ...

}

@Bean
@Singleton
public class Consumer {

	@Inject
	MyDuplicateBean bean; // Most likely will fail - will only work if there is only one MyBlueprint instance provided
	
	@InjectAll
	List<MyDuplicateBean> allbeans; // Will work - all copies of MyDuplicateBean will be provided
	
	@Inject
	@Named("copy1")
	MyDuplicateBean copy1Bean; // Will only work if one instance of MyBlueprint had the name "copy1"

}
```

Through the use of other qualifier, more granularity can be achieved, however the only way to inject one exact copy of a bean is through using `@Named` - meaning that the injection is highly intertwined with the blueprint configuration. This may be a viable solution in a tightly controlled situation, but not something that can be guaranteed.

```java
@Duplicate(MyBlueprint.class)
@Singleton
@Abc123
public class MyDuplicatedBean implements MyInterface {

	// ...

}

@Duplicate(MyBlueprint.class)
@Singleton
@Def456
public class MyOtherDuplicatedBean implements MyInterface {

	// ...

}

@Bean
@Singleton
public class Consumer {

	@InjectAll
	@Abc123
	List<MyInterface> abc123Beans; // In effect only MyDuplicateBeans will be present

	@InjectAll
	@Def456
	List<MyInterface> def456Beans; // In effect only MyOtherDuplicatedBeans will be present

}
```

#### Internal Injection
In this case "internal" is defined as meaning within the context of a given duplication group. In other words, injecting a bean which was created from the same blueprint instance. To achieve this, annotate the injection with `@Sibling` to make it clear, that the duplicate of the bean which belongs to the same instance of the same blueprint is desired.

```java
@Duplicate(MyBlueprint.class)
@Singleton
public class MyDuplicatedBean {

	// ...

}

@Duplicate(MyBlueprint.class)
@Singleton
public class MyOtherDuplicatedBean {

	@Inject
	@Sibling
	MyDuplicateBean bean;
	
	@Inject
	public MyOtherDuplicatedBean(@Sibling MyDuplicateBean bean) {
		// ...
	}

	// ...
}
```

This approach can also be employed to retrieve the actual blueprint that was used to create the duplicate copy and it is guaranteed that each sibling bean will have access to the same instance of the same blueprint.

```java
@Duplicate(MyBlueprint.class)
@Singleton
public class MyDuplicatedBean {

	@Inject
	@Sibling
	MyBlueprint blueprint;

	// ...

}

@Duplicate(MyBlueprint.class)
@Singleton
public class MyOtherDuplicatedBean {

	@Inject
	@Sibling
	MyDuplicateBean bean;
	
	@Inject
	public MyOtherDuplicatedBean(@Sibling MyBlueprint blueprint, @Sibling MyDuplicateBean bean) {
		assert(blueprint == bean.blueprint);
	}

	// ...
}
```

Once the blueprint is injected the bean can configure itself in whatever manner required using the values contained within the blueprint.

```java
@Duplicate(MyBlueprint.class)
@Singleton
public class MyDuplicatedBean {

	@Inject
	public MyDuplicatedBean(@Sibling MyBlueprint blueprint) {
		setData(blueprint.getData());
		// ...
	}

	// ...

}
```

### Nested Blueprints
Beyond the ability to define duplicate copies, blueprints also support inheritance where one blueprint can inherit from another. This allows for a nesting effect where a child duplicate can expand on the details of a parent and treat the duplicates of the parent as a sibling. These can be defined in the same project, or span across projects, so long as they are on the same classpath. To do so, the parent must implement `Blueprint` (or extend `BasicBlueprint`) the same way as any other dynamic duplicate. The child blueprint can then extend the parent blueprint and add any additional values as necessary.

```java
public class ParentBlueprint extends BasicBlueprint {
	private final int number;
	
	public Parent(String name, int number) {
		super(name);
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
}

public class ChildBlueprint extends ParentBlueprint {
	private final double dbl;
	
	public Parent(String name, int number, double dbl) {
		super(name, number);
		this.dbl = dbl;
	}
	
	public double getDouble() {
		return dbl;
	}
}
```

With the blueprint hierarchy thus established, we can continue on to build up the specific bean as per any other blueprint.

```java
@Duplicate(ParentBlueprint.class)
@Singleton
public class ParentDuplicate {
	// snip
}

@Duplicate(ChildBlueprint.class)
@Singleton
public class ChildDuplicate {
	// snip
}
```

By placing the blueprints into this hierarchy it does however mean, that for every `ChildBlueprint` a corresponding `ParentBlueprint` instance will be created, thus allowing the _parent to be injected as a sibling into the child_ and creating this nesting of duplicates.

```java
@Duplicate(ChildBlueprint.class)
@Singleton
public class ChildDuplicate {

	@Inject
	@Sibling
	ParentDuplicate parent;
}
```

Note that this is a "one way street" as there is no guarantee that every `ParentDuplicate` has a corresponding `ChildDuplicate` as there may very well be parent instances that are created independently of the child. Thus attempting to `inject` a child into the parent is only possible if there a ***guarantee*** in the client code that the only parent instances are created in response to the child duplicates.

```java
@Duplicate(ParentBlueprint.class)
@Singleton
public class ParentDuplicate {
	
	@Inject
	@Sibling
	ChildDuplicate child;
}

// The will work as there are no explicit Parent duplicates
ApplicationContextBuilder builder = new ApplicationContextBuilder();
builder.addDynamicDuplicate(new ChildBlueprint("a", 1, 1.0);
builder.addDynamicDuplicate(new ChildBlueprint("b", 2, 2.0);
builder.addDynamicDuplicate(new ChildBlueprint("c", 3, 3.0);
ApplicationContext ctx = builder.build();
ctx.start();


// Adding a single Parent duplicate will break as parent d has not corresponding child
ApplicationContextBuilder builder = new ApplicationContextBuilder();
builder.addDynamicDuplicate(new ChildBlueprint("a", 1, 1.0);
builder.addDynamicDuplicate(new ChildBlueprint("b", 2, 2.0);
builder.addDynamicDuplicate(new ChildBlueprint("c", 3, 3.0);
builder.addDynamicDuplicate(new ParentBlueprint("d", 4);
ApplicationContext ctx = builder.build();
ctx.start();
```

Or to put it more accurately, `Tendril` will make every effort to fulfill the sibling injection, however if there any `ParentBlueprints` created independently of `ChildBlueprint`, then attempting to inject the `ChildBlueprint` derived sibling into the `ParentBlueprint` derived bean will fail. As such, this is not something that should be done, unless in a very controlled environment (and at that point it would probably be a better idea to simply merge the two blueprints to avoid this issue in the future).

## Replacing Beans
There are situations where different beans are to be used in different circumstances (i.e.: database connection in production and a flat-file loader in dev/test). One option to achieve this would be to use _requirements_, such that a different environment is applied in different circumstances. While this will definitely achieve the desired goal, it can become a bit cumbersome. Specifically the different environments must be applied to the beans and then specified when launching the application, opening the door to errors that commonly occur when values are defined/applied in multiple places. It can also lack the finesse to target specific individual beans, namely all beans employing a different environment are impacted rather than just the specific individual bean that may be desired. In order to target a specific bean, an environment must be defined/applied for that specific bean, which can see the number of environments grow to unmanageable levels very quickly. The other major problem with this approach, is that this only work if you are in a position to modify the original bean. If you are loading a bean from a library that you do not control, you have no ability to add a environmental requirement to said bean.

For all of the above reasons, there exists the ability to replace a single targeted bean with a _like for like_ swap using `@Replaces`. Using this annotations marks a class as a bean, but one which must replace another pre-existing bean. Any _qualifiers_ applied to the replacement bean are used to find the original (it does not need to include all of the original bean qualifiers, however it must be able to uniquely identify a single bean), and once the original is located it is replaced. There are a few caveats that must be kept in mind:
* The replacement bean must be assignable to the original (i.e.: `Bean original = replacement;` must be valid).
* The replacement bean will be "shared" under its own type, meaning that it is technically valid to `@Inject` using the replacement class when the replacement is in use. Note that this should be avoided, as this will cease to be valid if the replacement is no longer in use.
* The quantifier of the replacement will be used, rather than that of the original.
* All qualifiers of the original will be applied to its replacement, to ensure a seamless transition.
* Requirements can be applied to the replacement, narrowing or controlling the situations when it is employed.

The `@Replaces` is employed in the exact same manner as `@Bean`, with the exception of replacement behavior.

```java
@Bean
@Singleton
@Named("MyName")
@MyQualifier
public class Original {
}

@Replaces
@Factory
public class Replacement extends Original {
}
```

The above will replace the `Original` with the `Replacement`, provided that `Original` is the only class that matches the `Replacement` qualifiers as presented. If beans exist of ancestors of `Original` (or other beans of `Original` exist), then additional qualifiers will need to be employed to ensure that it will resolve uniquely.

```java
@Bean
@Singleton
@Named("Parent")
public class Parent {
}

@Bean
@Singleton
@Named("MyName")
@MyQualifier
public class Original extends Parent {
}

@Replaces
@Factory
@MyQualifier
public class Replacement extends Original {
}
```

By default the replacement bean will be advertised as itself, following the same rules as `@Bean` (i.e.: the class or method return type it is applied to). Following in the `@Bean` parallel, `@Replaces` also allows for the advertised type of the replacement bean to be overridden by supplying the "override type" as a parameter to the annotation. For example, this allows for the override to "masquerade" as the original type and thus completely obfuscate that the replacement took place.

```java
@Replaces(Original.class)
@Factory
public class Replacement extends Original {
}
```

The same rules for the override class apply to `@Replaces` as apply to `@Bean`, namely that it must be a valid parent class or interface of the actual replacement bean instance, however it has one additional stipulation. The override class must also be a valid parent class/interface of the original bean as well. In otherwords, the override must be a common ancestor of both the replacement bean as well as the original one.

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
The dependency injection takes place within a `Context`, which must be created and started. This is done via `ApplicationContextBuilder`, which is to be created and started in the global application `main()` or equivalent.

```java
public static void main(String[] args) {
  ApplicationContextBuilder builder = new ApplicationContextBuilder();
  ApplicationContext ctx = builder.build();
  ctx.start();
}
```
`ApplicationContextBuilder::build()` taking care to build and configure the `ApplicationContext` as desired, with the `ApplicationContext::start()` left with the responsibility to find and `run()` the `Runner` that is defined, triggering assembly and execution of the `Tendril` application. It is then through this `ApplicationContext` that it is possible to manually interact or manipulate the beans separately from dependency injection. This includes injection beans which are not part of the DI system prior to starting the application.

```java
ApplicationContext ctx = builder.build();
ctx.registerBean(myExternalObject, new Descriptor<ExternalObject>(ExternalObject.class).setName("myName"));
ctx.start();
```

It is also possible to inject the `ApplicationContext` as a bean and then "manually" retrieve a bean without going through the "normal" injection mechanism.

```java
@Bean
@Singleton
public class MyClass {
	@Inject
	ApplicationContext ctx;
	
	@PostConstruct
	public void init() {
		List<String> allStrings = ctx.getAllBeans(new Descriptor<>(String.class));
		for (String s: allString)
			System.out.println("String Bean: " + s);
	}
}
```

## Testing
It's possible to create unit tests for `Tendril` applications, such that the application context is created, the injections fulfilled and made available in the test. This can be used to inject any beans which are available in the `ApplicationContext` such that they can be employed in the unit test. For this to work the `tendril-junit5` project must be added as the `testAnnotationProcessor`.

```groovy
dependencies {
    annotationProcessor('io.github.cancech:tendril-di') // For "non-test"
    testAnnotationProcessor('io.github.cancech:tendril-junit5') // For "test"
}
```

This requires applying the `@TendrilTest` annotation to the test class. When done, the annotated test is used "in lieu" of a `TendrilRunner` and no `TendrilRunner` is created or loaded.

```java
@TendrilTest
public class ExampleTest {
	@Inject
	ApplicationContext ctx;
	@Inject
	MyBean myBean;
	
	@InjectAll
	List<Object> allBeans;
	
	@Test
	public void testBeansCreated() {
		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(myBean);
		Assertions.assertNotNull(allBeans);
		
		Assertions.assertEquals(2, allBeans.size());
		CollectionAssert.assertEquivalent(allBeans, ctx, myBean);
	}
}
```

The test class prepared in this manner be injected via `@Inject`, however it cannot provide any beans. It is however possible for beans to be prepared in the test project code (outside of the test class), whether that be as "new beans" or as replacements for a "real bean".

### Customize Environments for the Test
As the tests do not follow "normal" execution procedures, the mechanism through which environments are specified or applied in the application runtime cannot (easily) be applied to the test `ApplicationContext`. Short of executing the tests with the appropriate `-Denvironments` parameter, however this would apply the specified environments to all tests and prevent testing different environment configurations in a single test run. In other words, different test executions would need to be performed for the different desirable environment configurations, which can very quickly become unmanageable depending on how the tests are organized. To this end, the `TendrilTest` annotation allows for the desired environments to be specified on a per-test class basis via its `environments` attribute. In this way, each test class can have a different set of environments applied, allowing for quick and easy testing of different environment permutations. By default, if the parameter is not "overridden" then no environments are applied to the test.

```java
@TendrilTest
public class NoEnvTest {
}

@TendrilTest(environments = "A")
public class EnvATest {
}

@TendrilTest(environments = "B")
public class EnvBTest {
}

@TendrilTest(environments = {"A", "B"})
public class EnvAandBTest {
}
```

### Customize Properties for the Test
Much like what is described for the Environments above, the same issue and ultimately solution exists for Properties. The `TendrilTest` provides a `properties` attribute which can be used to specify which properties are to be applied to a given test class. If let unspecified, it will default to "no additional properties" beyond what is otherwise specified in the JVM.

```java
@TendrilTest
public class NoPropTest {
}

@TendrilTest(properties = "A")
public class PropATest {
}

@TendrilTest(properties = "B")
public class PropBTest {
}

@TendrilTest(properties = {"A", "B"})
public class PropAandBTest {
}
```

### Customize Duplication Blueprints
When dynamic duplicates are to be encorporated into the test, the normal approach of simply telling the `ApplicationContextBuilder` what `BlueprintDriver` instances to employ does not work as the `ApplicationContext` is created automatically without any immediate or direct input from the client code. As such, a `@TestBlueprints` annotation is incorporated through which the test can be notified of what blueprint drivers to employ. This can be placed on a method in the test class and the unit test will automatically call it to the `BlueprintDriver`s it provides. Note there are a couple of mandatory stipulations that must be followed:

1. The method must be `public` and it must be `static`.
2. The method must return a `List<BlueprintDriver>`.
3. The method cannot take any parameters.
4. The name of the method is irrelevant, though must follow proper Java standards (i.e.: it has to compile of course)

Failure to adhere to the above stipulations will result in an error when running the test.

```java
@TendrilTest
public class MyTest {
	@TestBlueprints
	public static List<BlueprintDriver> getTestBlueprints() {
		return Arrays.asList(new MyBlueprint("abc123"), new MyBlueprint("def456"));
	}
}
```

This allows for a different set of `BlueprintDriver`s to be used in different tests.

### Extending Test Classes
It is possible to extend a base test class, such that it can both provide a common test core, as well as tackle common/shared elements. For example

```
@TendrilTest(environments = {"A", "B"}, properties = {"C", "D"})
public class BaseTest {
	@Inject
	MyBean bean;
}

public class ConcreteTest extend BaseTest {
	@Inject
	MyOtherBean otherBean;

	@Test
	public void test() {
		Assertions.assertNotNull(bean);
		Assertions.assertNotNull(otherBean);
		Assertions.assertNotEqual(bean, otherBean);
	}
}
```

In this case both `BaseTest` and `ConcreteTest` will be executing using the `@TendrilTest` configuration with environments `A` and `B` as well as the properties `C` and `D` applied to the `ApplicationContext`. The same applies to `@TestBlueprints` blueprint creation methods. All such methods which appear in the inheritance hierarchy of a given test class will be combined into a "superset" of blueprint drivers for the current class. This can be used to either create a parent class which prepares the blueprints for a number of tests

```java
public class BaseTest {
	@TestBlueprints
	public static List<BlueprintDriver> getTestBlueprints() {
		return Arrays.asList(new MyBlueprint("abc123"), new MyBlueprint("def456"));
	}
}

@TendrilTest
public class ConcreteTest extends BaseTest {
	// Will have the blueprints defined in BaseTest.getTestBlueprints()
}
```

or allow for one test to "augment" the blueprints from another test.

```java
public class BaseTest {
	@TestBlueprints
	public static List<BlueprintDriver> getBaseBlueprints() {
		return Arrays.asList(new MyBlueprint("abc123"), new MyBlueprint("def456"));
	}
}

@TendrilTest
public class ConcreteTest extends BaseTest {
	@TestBlueprints
	public static List<BlueprintDriver> getMoreBlueprints() {
		return Arrays.asList(new MyBlueprint("321cba"), new MyBlueprint("654fed"));
	}

	// Will have the blueprints defined in BaseTest.getBaseBlueprints() as well as those defined in ConcreteTest.getMoreBlueprints()
}
```

## Known Issues and Limitations
While every effort is made to provide a fully functional capability and address all issues, there are some which have not been addressed as they would be too invasive to fix and ultimately not worth the effort at this stage. These are issues and limitations are documented here, so that the appropriate mitigation steps can be taken in the client code.

### Repeat bean methods in a Configuration
While it is technically fully legal to have overloaded methods in a `Configuration` file, such that each overload produces a different instance/variation of a bean, or a completely different type of bean altogether, this is something that is not handled properly within `Tendril` and will result in an exception during annotation processing

```
javax.annotation.processing.FilerException: Attempt to recreate a file for type <configuration><method>Recipe
```

For example the following code, while technically legal, will result in this exception being thrown.

```java
@Configuration
public class TestConfig {

	@Bean
	@Singleton
	MyBean createBean() {
		return new MyBean("NoArgBean");
	}
	
	@Bean
	@Singleton
	MyBean createBean(String msg) {
		return new MyBean(msg);
	}
}
```

`Tendril` automatically generates the recipe class `TestConfigcreateBeanRecipe` for both `createBean` methods, resulting in the above exception. The work around is to ensure that each bean method in a single `Configuration` class has a unique name. Thus, the above can be updated as follows

```java
@Configuration
public class TestConfig {

	@Bean
	@Singleton
	MyBean createNoArgBean() {
		return new MyBean("NoArgBean");
	}
	
	@Bean
	@Singleton
	MyBean createMsgBean(String msg) {
		return new MyBean(msg);
	}
}
```

### Repeat class names
Tendril assumes that every class name is always unique, ergo it can be imported. As such, if a situation arises where the same class name appears on two distinct classes in the same bean a name clash will ensue and the generated code will not work. The work around for this is to ensure that all classes that appear within a single bean are unique.

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
|[tendril-junit5](./tendril-junit5)|JUnit5 integration library, to allow for the creation of JUnit5 tests that run within an `ApplicationContext`.|
|[tendril-test](./tendril-test)|Automated/Unit test library, to facilitate and simplify the creation of JUnit tests.|
|[tendril-test-app](./tendril-test-app)|A sample application which employs `Tendril`, primarily for the purpose of providing a test bed in which the various capabilities can be tested and verified.|
