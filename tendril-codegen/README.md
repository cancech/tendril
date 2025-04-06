# Tendril Code Generator
This library serves a dual purpose, one to create a representation of Java classes and contained elements, and two to generate code from said representation. It is this representation which is employed by `tendril-annotation-processor` and consequently `tendril-di` when "understanding" the elements which the annotation processor is processing, as well as by `tendril-di` for the purpose of defining and generating the resulting code of such processing. This library provides a variety of factories and builders, through which the representation of Java classes can be achieved, and once this representation is created it can "generate itself" into proper Java code.

## Representing Types


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
