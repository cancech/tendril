# Tendril
Tendril is an Annotation Processing based Dependency Injection, where the necessary code to perform dependency injection is generated at compile time and the dependency injection itself at runtime - no reflection required. The core usage is fairly straightforward, where a bean is defined in one location and then consumed in another. Two annotations are employed to indicate the "direction" of bean movement:

|Annotation|Description|
|---       | ---       |
|`@Bean`|Indicates the *production* of a bean, ergo a bean is created (outgoing).|
|`@Inject`|Indicates the *injection* of a bean, ergo a bean is consumed/retrieved (incoming).|

Since reflection is not employed, all beans (and any `Tendril` facing aspects) cannot be private. They must be at least `package private` in order for dependency injection to be successfully performed.

## Definitions
Some of these may be clear, some not, but for sake of completeness terms are defined here for easy reference.

|Term|Definition|
|--- | ---      |
|Bean|An object instance which is passed around via Dependency Injection|
|Configuration|A class which performs the steps to create and provide one or more Beans that are separate from the Configuration class itself.|
|Dependency Injection|A mechanism for passing object from producers to consumers, such that consumers can obtain their necessary dependencies without needing to be concerned with where said dependencies come from.|
|Qualifier|Annotation applied to a Bean to add descriptive information to the Bean, to make it possible/easier to find specific Beans (akin to metadata).|
|Quantifier|Annotation applied to a Bean to indicate how many copies of the Bean are to be produced.|
|Recipe|Generated class which contains the steps necessary to build a Bean within the `Tendril` framework.|

## Creating a Bean
The bean is the key ingredient in any Dependency Injection scheme, and `Tendril` is no different. There are two ways in which to define beans:
1. Within a class (where the class defines itself as a bean)
2. Within a `Configuration`

Regardless of which approach is taken to define a bean, each bean must be *quantified*, with the appropriate or desired *quantifier* annotation. It must be one of the following:

|Annotation|Description|
|---       | ---       |
|`@Singleton`| The first time the bean is accessed, an instance is created. This instace is then returned for each subsequent access of the bean. `Tendril` *guarantees* that only a single instance of the bean is ever employed as part of dependency injection. The class itself need not (in fact should not) actually follow the Singleton pattern.|
|`@Factory`| A new instance of the class is created for every access of the bean. `Tendril` *guarantees* that the same approach/mechanism is employed for the purpose of creating the bean, but a separate copy is always retrieved.|

### Bean Class
The simplest and most straight forward to define a bean, is to make a class itself into a bean. Much like how a class defines the characteristics and capabilities of an enclosed *thing*, it can also define how it is to be used within `Tendril`. To do so, simply annotate the class with `@Bean` to indicate to `Tendril` that it is to be treated as a bean. Do not forget that the `quantifier` is still necessary. Thus a simple bean would look like the following:

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

Note that private constructors are not considered valid and are thus ignored for the purpose of bean processing. This means that if there is only a single non-private constructor, it need not be annotated with `@Inejct`. Any private constructors are ignored, regardless of whether or not they are annotated with `@Inject`.

### Configuration Class
Where a bean class defined itself as a bean, a `Configuration` defines other beans. Meaning that the class itself is not a bean, but rather it provides one or more beans via methods. To create a `Configuration` a class is annotated with `@Configuration` and any methods which are to be used as the source of beans must be annotated with `@Bean`.

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

In this situation, the `Configuration` will be initialized before any beans it provides are created, but only the beans it provides will be made available for injection (i.e.: the `Configuration` itself is transient, a means to an end). This allows for classes from outside of the codebase to be incorporated into `Tendril` dependency injeciton, as well as having multiple distinct copies of the same class to be made available as well. The same `Configuration` instance is employed for all beans it provides, meaning that the `Configuration` class itself is only initialized/created once.

## Consuming a Bean
In essence, the act of creating Beans is also the act of consuming them. Bean consumption is performed as part of Bean creation, where a Bean consumes its dependencies before it itself is provided onward to whomever depends on it. Thus, consuming a Bean is the act of defining what other Bean a given Bean depends on. This is done via the `@Inject` annotation, which can be applied on:
* Constructors - the parameters the constructor takes are treated as bean dependencies and provided when called
* Instance Fields - instance fields annotated with `@Inject` are automatically fulfilled by dependency injection
* Methods - any method annotated with `@Inject` is automatically called (once) as part of dependency injection, with all parameters it contains treated as dependencies that are to be provided.

This applied to both `Bean Classes` as well as `Configurations`.

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

Some things to note:
* as mentioned at the start, no injected element can be `private`
* per above, the `@Inject` annotation on a constructor is only necesseray if there are multiple valid constructors on a class. It is still recommended to always apply the annotation regardless to make it explicitely clear (both to `Tendril` as well as anyone looking at the code in the future).

## Bean Lifecycle

There is a rigid lifecycle that Beans adhere to within the `Tendril` ecosystem, starting from initialization all the way to destruction. No Bean is provided until all of its dependencies has been completely satisfied, ergo `Tendril` guarantees that any Bean which is injected is *complete* (as far as `Tendril` is concerned). The initialization order of a Bean is as follows:
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
