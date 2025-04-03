# Tendril
Tendril is an Annotation Processing based Dependency Injection, where the necessary code to perform dependency injection is generated at compile time and the dependency injection itself at runtime - no reflection required. The core usage is fairly straightforward, where a bean is defined in one location and then consumed in another. Two annotations are employed to indicate the "direction" of bean movement:

|Annotation|Description|
|---       | ---       |
|`@Bean`|Indicates the *production* of a bean, ergo a bean is created (outgoing).|
|`@Inject`|Indicates the *injection* of a bean, ergo a bean is consumed/retrieved (incoming).|

Since reflection is not employed, all beans (and any `Tendril` facing aspects) cannot be private. They must be at least `package private` in order for dependency injection to be successfully performed.

## Creating a Bean
The bean<sup>1</sup> is the key ingredient in any Dependency Injection scheme, and `Tendril` is no different. There are two ways in which to define beans:
1. Within a class (where the class defines itself as a bean)
2. Within a `Configuration`

<sup>1 - a bean is how an object which is passed around via dependency injection is referred to</sup>

### Bean Class
The simplest and most straight forward to define a bean, is to make a class itself into a bean. Much like how a class defines the characteristics and capabilities of an enclosed *thing*, it can also define how it is to be used within `Tendril`. To do so, simply annotate the class with `@Bean` to indicate to `Tendril` that it is to be treated as a bean. However, `Tendril` need to know how to manage the bean, so an additional annotation is required. This annotation is a *quantifier* and must be one of the following:

|Annotation|Description|
|---       | ---       |
|`@Singleton`| The first time the bean is accessed, an instance is created. This instace is then returned for each subsequent access of the bean. `Tendril` *guarantees* that only a single instance of the bean is ever employed as part of dependency injection. The class itself need not (in fact should not) actually follow the Singleton pattern.|
|`@Factory`| A new instance of the class is created for every access of the bean. `Tendril` *guarantees* that the same approach/mechanism is employed for the purpose of creating the bean, but a separate copy is always retrieved.|

Thus a simple bean would look like the following:

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

Note that private constructors are not considered valid and are thus ignored for the purpose of bean processing. This means that if there is only a single non-private constructor, it need not be annotated with `@Inejct`. Any constructors annotated with `@Inject` which are private are ignored.

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

In this situation, the `Configuration` will be initialized before any beans it advertises are created, but only the beans it provides will be made available for injection (i.e.: the `Configuration` itself is transient, a means to an end). This allows for classes from outside of the codebase to be incorporated into `Tendril` dependency injeciton, as well as having multiple distinct copies of the same class to be made available as well.
