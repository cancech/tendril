# Tentril Test
A reusable library containing test features that are shared and used across the `Tendril` libraries. This is primarily in the form of a reusable [AbstractUnitTest](./tendril-test/src/main/java/tendril/test/AbstractUnitTest.java), which can be used as the base class and facilitate the handling and management of Mockito Mocks in a unit test.

## AbstractUnitTest
The main feature that the `AbstractUnitTest` brings, is the ability to en-masse perform a check to ensure that all Mocks have been properly verifies. It tracks all mocks that are created and as part of the test termination ensure that there are no unverified interaction on any of them, failing the test if there are. To facilitate setup and teardown appropriate methods `prepareTest()` and `cleanupTest()` are available for override to perform these tasks. The verification of mocked interactions is performed:
* after calling `prepareTest()`
* after the test itself completes (before calling `cleanupTest()`)
* after calling `cleanupTest()`

It can be triggered manually as well by calling `verifyAllChecked()`. An example unit test looks like:

```java
public class MyClassTest extends AbstractUnitTest {

  @Mock
  private MyOtherClass mockOtherClass;
  @Mock
  private MyFeature mockFeature;

  private MyClass instance;

  @Override
  protected void prepareTest() {
    instance = new MyClass(mockOtherClass, mockFeature);
    verify(mockOtherClass).doSomething();
  }

  @Override
  protected void cleanupTest() {
    instance.cleanup();
  }

  @Test
  public void test() {
    instance.doSomething();
    verify(mockFeature).doSomething();
  }
}
```

## Assertions
The other main capability that `tendril-test` brings are a number of custom assertions which can be employed as part of unit tests.

|Assertion Class|Description|
|---            | ---       |
|[ClassAssert](./tendril-test/src/main/java/tendril/test/assertions/ClassAssert.java)|Assertions relating to the type of an object|
|[CollectionAssert](./tendril-test/src/main/java/tendril/test/assertions/CollectionAssert.java)|Assertions relating to `Collections`|

### String Matchers
Related to, but not quite assertions are the available matchers for `Strings`. These come in two forms: single line and multi-line matchers. Single line matchers can be accessed via [StringMatcher](./tendril-test/src/main/java/tendril/test/assertions/matchers/StringMatcher.java), with the ability to create `regex` or `equality` matchers. Multi-line matchers are provided via the [MultiLineStringMatcher](./tendril-test/src/main/java/tendril/test/assertions/matchers/MultiLineStringMatcher.java), where an instance of the matcher is created and line by line the expected `String` is described as either the `regex` it should match, or the exact `String` that should be present. Regardless of the type, verification is performed by calling `matches(actual)`.
```java
StringMatcher.eq("exact expected").matches(actual);
StringMatcher.regex("regex expected").matches(actual);

MultiLineStringMatcher matcher = new MultiLineStringMatcher();
matcher.eq("exact line");
matcher.regex("regex line");
matcher.matches(actual);
```
