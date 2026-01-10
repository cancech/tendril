# Tendril Code Generator
This library serves a dual purpose, one to create a representation of Java classes and contained elements, and two to generate code from said representation. It is this representation which is employed by `tendril-annotation-processor` and consequently `tendril-di` when "understanding" the elements which the annotation processor is processing, as well as by `tendril-di` for the purpose of defining and generating the resulting code of such processing. This library provides a variety of factories and builders, through which the representation of Java classes can be achieved, and once this representation is created it can "generate itself" into proper Java code.

## Representing Types
The `Type` interface is used to represent all supported types, which can be one of the following:
* `ArrayType` - array of `Type`
* `ClassType` - representing a class (package and class name)
* `GenericType` - representing a generic
* `PrimitiveType` - enumeration of the various Java primitives
* `VoidType` - representation of `void` (primarily for the purpose of depictive `void` return types)

These are then used throughout to represent all manner of types, including (but not limited to):
* variables (fields/parameters)
* method return
* classes

## Building a Class
When building a class it is easiest to start from the `ClassBuilder` and leverage one of the static methods indicative of the type of class to be built:
* `ClassBuilder.forConcreteClass()` - a "normal" class (`class MyClass`)
* `ClassBuilder.forAbstractClass()`
* `ClassBuilder.forInterface()`
* `ClassBuilder.forAnnotation()`
* `ClassBuilder.forEnum()`
The selection will dictate what is generated, what the resulting class will look like, and what is/isn't allowed for be included, however the process is larely unchanged. The different method of the builder can then be employed to define what the class looks like and what it contains. Once satisfied with everything that has been defined on the class, simply calling `ClassBuilder.build()` will create the desired `JClass` representation of the defined class.

### Add a Nested Element
Adding a nested element (i.e.: constructor, field, or method) to a `ClassBuilder` is as simple as calling `classBuilder.build<Element>()` (i.e.: `classBuilder.buildConstructor()`, `classBuilder.buildField()`, or `classBuilder.buildMethod()`). This will return a the appropriate builder through which the details of the nested element can be specified. Once the element has been fully defined, either `finish()` or `build()` can be called. Both will add the element to the class defined in the `ClassBuilder`, with the only difference being what is returned. `finish()` will return the `ClassBuilder`, allowing for continued chaining of calls to further build-up the class, whereas `build()` will return the element created (i.e.: `JConstructor`, `JField`, or `JMethod`).

If the nested element is a constructor or method, there is one additional step that can be performed as compared to a field. That of adding an implementation. By default the implementation is *empty* (read `abstract`), and an implementation must be added if is not to be `abstract`. This can be done by calling either `emptyImplementation()` which will mark the element has having an implementation albeit an empty one, or `addCode()` which will add the indicated lines of code to the implementation. There is no limit as to the number of lines which can be added, but note that the code is added **verbatim**. `emptyImplementation()` will also clear/remove any added code, if any code had already been added. 

### Add an Annotation
Annotations are a bit of an exception to the overall paradigm, as they can exist as either a definition (i.e.: class) or an applied instance (i.e.: `@Annotation` applied to a class/method/etc). This dichotomy is represented by having two different implementations, one for each of the different possibilities. `JClassAnnotation` represents the class definition and `JAnnotation` represents the annotation when applied to an element. They are also created in different ways, with `ClassBuilder.forAnnotation()` resulting in the creation in `JClassAnnotation`, and `JAnnotationFactory` triggering the creation of `JAnnotation`. It is important to note that `JClassAnnotation` is only intended to be used when defining the annotation class (i.e.: for the purpose of generating a new annotation), while `JAnnotation` is what is what a builder looks for when applying an annotation to an element under construction (whether that be a class, method, field, or the like).

### Add a Value
Values can be added to some elements, such as fields and annotations. Values are represented by `JValue` and can be created in a variety of ways. If the `Type` of the value is known, the simplest is to simply call `Type.asValue()`. The concrete `Type` will attempt to convert the provided `Object` to a `JValue` of the `Type`. Another option is to use `JValueFactory`, which can "convert" an `Object` value to a `JValue` for arrays, enums, and primitives.

### Example
A comprehensive example is the following
```java
GenericType genericT = GenericFactory.create("T");
GenericType genericU = GenericFactory.createExtends("U", TypeFactory.createClassType("a", "B"));
ClassType listClass = TypeFactory.createClassType(List.class, GenericFactory.createSuper(TypeFactory.createClassType("z.x.c", "V")));
ClassType stringClass = TypeFactory.createClassType(String.class);

JClass parentCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).build();
JClass ifaceYCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("q.w.e.r.t", "Y")).build();
JClass ifaceFCls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("a.b.c.d", "F")).build();
JClass cls = ClassBuilder.forConcreteClass(TypeFactory.createClassType("z.x.c.v", "B")).setVisibility(VisibilityType.PROTECTED)
  .extendsClass(parentCls)
  .implementsInterface(ifaceYCls).implementsInterface(ifaceFCls)
  .buildMethod(PrimitiveType.CHAR, "charMethod").setVisibility(VisibilityType.PROTECTED)
      .buildParameter(stringClass, "strParam").finish().emptyImplementation().finish()
  .addAnnotation(JAnnotationFactory.create(Deprecated.class, Map.of("since", JValueFactory.create("yesterday"), "forRemoval", JValueFactory.create(true))))
  .buildMethod(PrimitiveType.LONG, "longMethod").setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
      .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
  .addAnnotation(JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", JValueFactory.create("qwerty"), "valInt", JValueFactory.create(789))))
  .buildField(PrimitiveType.BOOLEAN, "booleanField").setVisibility(VisibilityType.PUBLIC).setValue(JValueFactory.create(false)).finish()
  .buildField(VisibilityType.class, "enumField").setVisibility(VisibilityType.PRIVATE).setValue(JValueFactory.create(VisibilityType.PACKAGE_PRIVATE)).finish()
  .buildConstructor().setVisibility(VisibilityType.PUBLIC).emptyImplementation().finish()
  .buildConstructor().addGeneric(genericU).setVisibility(VisibilityType.PROTECTED)
      .buildParameter(stringClass, "strParam").finish()
      .buildParameter(genericU, "u").finish()
      .addCode("a", "b", "c", "d").finish()
  .buildConstructor().setVisibility(VisibilityType.PRIVATE).addCode("abc", "123", "qwerty")
      .addAnnotation(JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, Map.of("myString", JValueFactory.create("qazwsx")))).finish()
  .buildMethod("abc123").addGeneric(genericT).addGeneric(genericU).buildParameter(genericT, "t").finish().buildParameter(genericU, "u").finish()
      .buildParameter(listClass, "list").finish().addCode().finish()
  .buildField(genericT, "tField").finish()
  .buildField(listClass, "listField").finish()
  .build();
```
which describes the following class:
```java
package z.x.c.v;

import a.B;
import a.b.c.d.F;
import java.util.List;
import javax.annotation.processing.Generated;
import q.w.e.r.t.Y;
import tendril.codegen.VisibilityType;
import tendril.test.helper.annotation.TestMultiAttrsAnnotation;
import tendril.test.helper.annotation.TestNonDefaultAttrAnnotation;
import z.x.c.V;

@Generated(date = "2025-04-06T14:37:12.531893122", value = "tendril")
@Deprecated(forRemoval = true, since = "yesterday")
@TestMultiAttrsAnnotation(valInt = 789, valStr = "qwerty")
protected class B extends Y implements Y, F {
  public boolean booleanField = false;

  private VisibilityType enumField = VisibilityType.PACKAGE_PRIVATE;

  T tField;

  List<? super V> listField;

  public B() {
  }

  protected <U extends B> B(String strParam, U u) {
      a
      b
      c
      d
  }

  @TestNonDefaultAttrAnnotation(myString = "qazwsx")
  private B() {
      abc
      123
      qwerty
  }

  protected char charMethod(String strParam) {
  }

  @TestNonDefaultAttrAnnotation(myString = "qazwsx")
  private long longMethod() {
      abc
      123
      qwerty
  }

  <T, U extends B> void abc123(T t, U u, List<? super V> list) {
  }
}
```
Additional examples of this in action can be seen in various places in the code, primarily in the `tendril-codegen` unit tests as well as `tendril-di` for the purpose of loading and generating code.

## Generating Code
Once a `JClass` is defined and built, generating its code is as simple as calling `JClass.generateCode()`. This produces a multi-line `String` containing the entire code for the `JClass`. It is possible to generate the code of individual elements (rather than the entire class) by calling `generateSelf()` on the element in question.
