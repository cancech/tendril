/*
 * Copyright 2024 Jaroslav Bosak
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
package tendril.codegen.annotation;

import java.util.Collections;
import java.util.Map;

import javax.annotation.processing.Generated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.DefinitionException;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;
import tendril.test.helper.annotation.TestDefaultAttrAnnotation;
import tendril.test.helper.annotation.TestMarkerAnnotation;
import tendril.test.helper.annotation.TestMultiAttrsAnnotation;
import tendril.test.helper.annotation.TestNonDefaultAttrAnnotation;
import tendril.test.helper.assertions.TendrilAssert;

/**
 * Test case for the {@link JAnnotationFactory}
 */
public class JAnnotationFactoryTest extends AbstractUnitTest {

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationFromClass() {
        JAnnotation annotation = JAnnotationFactory.create(TestMarkerAnnotation.class);
        assertImportData(TestMarkerAnnotation.class, annotation);
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationFromFullyQualifiedName() {
        JAnnotation annotation = JAnnotationFactory.create(TestMarkerAnnotation.class.getName());
        assertImportData(TestMarkerAnnotation.class, annotation);
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationFromSplitName() {
        JAnnotation annotation = JAnnotationFactory.create(TestMarkerAnnotation.class.getPackageName(), TestMarkerAnnotation.class.getSimpleName());
        assertImportData(TestMarkerAnnotation.class, annotation);
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationFromClassType() {
        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType(TestMarkerAnnotation.class));
        assertImportData(TestMarkerAnnotation.class, annotation);
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationWhenAllAttributesHaveDefaults() {
        JAnnotation annotation = JAnnotationFactory.create(Deprecated.class);
        assertImportData(Deprecated.class, annotation);
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations can be created properly
     */
    @Test
    public void testCreateMarkerAnnotationWithUnknownClass() {
        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType("a.b.c.D"));
        TendrilAssert.assertImportData("a.b.c", "D", annotation.getType());
        CollectionAssert.assertEmpty(annotation.getAttributes());
    }

    /**
     * Verify that marker annotations cannot have any attributes
     */
    @Test
    public void testCreateMarkerAnnotationNotADefaultClass() {
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TypeFactory.createClassType(JAnnotation.class)));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestDefaultAttrAnnotation.class));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class));
    }

    /**
     * Verify that an annotation with a default attribute can be created properly
     */
    @Test
    public void testCreateDefaultValueAnnotationFromClass() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");

        JAnnotation annotation = JAnnotationFactory.create(TestDefaultAttrAnnotation.class, value);
        assertImportData(TestDefaultAttrAnnotation.class, annotation);
        CollectionAssert.assertEquals(Collections.singleton(new AnonymousMethod<>(value.getType(), "value")), annotation.getAttributes());
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
    }

    /**
     * Verify that an annotation with a default attribute can be created properly
     */
    @Test
    public void testCreateDefaultValueAnnotationFromFullyQualifiedName() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");

        JAnnotation annotation = JAnnotationFactory.create(TestDefaultAttrAnnotation.class.getName(), value);
        assertImportData(TestDefaultAttrAnnotation.class, annotation);
        CollectionAssert.assertEquals(Collections.singleton(new AnonymousMethod<>(value.getType(), "value")), annotation.getAttributes());
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
    }

    /**
     * Verify that an annotation with a default attribute can be created properly
     */
    @Test
    public void testCreateDefaultValueAnnotationFromSplitName() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");

        JAnnotation annotation = JAnnotationFactory.create(TestDefaultAttrAnnotation.class.getPackageName(), TestDefaultAttrAnnotation.class.getSimpleName(), value);
        assertImportData(TestDefaultAttrAnnotation.class, annotation);
        CollectionAssert.assertEquals(Collections.singleton(new AnonymousMethod<>(value.getType(), "value")), annotation.getAttributes());
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
    }

    /**
     * Verify that an annotation with a default attribute can be created properly
     */
    @Test
    public void testCreateDefaultValueAnnotationFromClassType() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");

        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType(TestDefaultAttrAnnotation.class), value);
        assertImportData(TestDefaultAttrAnnotation.class, annotation);
        CollectionAssert.assertEquals(Collections.singleton(new AnonymousMethod<>(value.getType(), "value")), annotation.getAttributes());
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
    }

    /**
     * Verify that a default annotation is created even if the class cannot be found for it
     */
    @Test
    public void testCreateDefaultAnnotationWithUnknownClass() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");

        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType("a.b.c.D"), value);
        TendrilAssert.assertImportData("a.b.c", "D", annotation.getType());
        CollectionAssert.assertEquals(Collections.singleton(new AnonymousMethod<>(value.getType(), "value")), annotation.getAttributes());
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
    }

    /**
     * Verify that an annotation with a default attribute must have exactly one "value" attribute
     */
    @Test
    public void testCreateDefaultValueAnnotationNotADefaultClass() {
        JValue<ClassType, String> value = JValueFactory.create("abc123");
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMarkerAnnotation.class, value));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, value));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestNonDefaultAttrAnnotation.class, value));
    }

    /**
     * Verify that an annotation with a multiple attributes can be created properly
     */
    @Test
    public void testCreateMultiValueAnnotationFromClass() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        JAnnotation annotation = JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", strValue, "valInt", intValue));
        assertImportData(TestMultiAttrsAnnotation.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(strValue.getType(), "valStr"), new AnonymousMethod<>(intValue.getType(), "valInt"));
        Assertions.assertEquals(strValue, annotation.getValue(new AnonymousMethod<>(strValue.getType(), "valStr")));
        Assertions.assertEquals(intValue, annotation.getValue(new AnonymousMethod<>(intValue.getType(), "valInt")));
    }

    /**
     * Verify that an annotation with a multiple attributes can be created properly
     */
    @Test
    public void testCreateMultiValueAnnotationFromFullyQualifiedName() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        JAnnotation annotation = JAnnotationFactory.create(TestMultiAttrsAnnotation.class.getName(), Map.of("valStr", strValue, "valInt", intValue));
        assertImportData(TestMultiAttrsAnnotation.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(strValue.getType(), "valStr"), new AnonymousMethod<>(intValue.getType(), "valInt"));
        Assertions.assertEquals(strValue, annotation.getValue(new AnonymousMethod<>(strValue.getType(), "valStr")));
        Assertions.assertEquals(intValue, annotation.getValue(new AnonymousMethod<>(intValue.getType(), "valInt")));
    }

    /**
     * Verify that an annotation with a multiple attributes can be created properly
     */
    @Test
    public void testCreateMultiValueAnnotationFromSplitName() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        JAnnotation annotation = JAnnotationFactory.create(TestMultiAttrsAnnotation.class.getPackageName(), TestMultiAttrsAnnotation.class.getSimpleName(),
                Map.of("valStr", strValue, "valInt", intValue));
        assertImportData(TestMultiAttrsAnnotation.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(strValue.getType(), "valStr"), new AnonymousMethod<>(intValue.getType(), "valInt"));
        Assertions.assertEquals(strValue, annotation.getValue(new AnonymousMethod<>(strValue.getType(), "valStr")));
        Assertions.assertEquals(intValue, annotation.getValue(new AnonymousMethod<>(intValue.getType(), "valInt")));
    }

    /**
     * Verify that an annotation with a multiple attributes can be created properly
     */
    @Test
    public void testCreateMultiValueAnnotationFromClassType() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType(TestMultiAttrsAnnotation.class), Map.of("valStr", strValue, "valInt", intValue));
        assertImportData(TestMultiAttrsAnnotation.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(strValue.getType(), "valStr"), new AnonymousMethod<>(intValue.getType(), "valInt"));
        Assertions.assertEquals(strValue, annotation.getValue(new AnonymousMethod<>(strValue.getType(), "valStr")));
        Assertions.assertEquals(intValue, annotation.getValue(new AnonymousMethod<>(intValue.getType(), "valInt")));
    }

    /**
     * Verify that a multi value annotation is created even if the class cannot be found for it
     */
    @Test
    public void testCreateMultiValueAnnotationWithUnknownClass() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        JAnnotation annotation = JAnnotationFactory.create(TypeFactory.createClassType("a.b.c.D"), Map.of("valStr", strValue, "valInt", intValue));
        TendrilAssert.assertImportData("a.b.c", "D", annotation.getType());
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(strValue.getType(), "valStr"), new AnonymousMethod<>(intValue.getType(), "valInt"));
        Assertions.assertEquals(strValue, annotation.getValue(new AnonymousMethod<>(strValue.getType(), "valStr")));
        Assertions.assertEquals(intValue, annotation.getValue(new AnonymousMethod<>(intValue.getType(), "valInt")));
    }

    /**
     * Verify that a marker annotation cannot be used to create a multi value annotation
     */
    @Test
    public void testCannotCreateMultiValueFromMarker() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMarkerAnnotation.class, Map.of("valStr", strValue, "valInt", intValue)));
    }

    /**
     * Verify that a attribute mismatch will produce an error
     */
    @Test
    public void testCannotCreateMultiValueAnnotationValueMismatch() {
        JValue<ClassType, String> strValue = JValueFactory.create("abc123");
        JValue<PrimitiveType, Integer> intValue = JValueFactory.create(234);

        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("strVal", strValue, "valInt", intValue)));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("valStr", strValue, "intVal", intValue)));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("strVal", strValue, "intVal", intValue, "extra", intValue)));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("strVal", strValue)));
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(TestMultiAttrsAnnotation.class, Map.of("strVal", intValue, "valInt", strValue)));
    }

    /**
     * Verify that can create an annotation with defaults
     */
    @Test
    public void testMultiValueAnnotationWithDefaults() {
        verifyMultiValueAnnotationWithOptionalArray(JValueFactory.createArray("value"));
        verifyMultiValueAnnotationWithOptionalArray(JValueFactory.create("value"));
    }

    private <DATA_TYPE extends Type, VALUE> void verifyMultiValueAnnotationWithOptionalArray(JValue<DATA_TYPE, VALUE> value) {
        JValue<ClassType, String> date = JValueFactory.create("date");
        JValue<ClassType, String> comments = JValueFactory.create("comments");
        
        // None provided, exception is thrown
        Assertions.assertThrows(DefinitionException.class, () -> JAnnotationFactory.create(Generated.class, Map.of()));

        // All defaults
        JAnnotation annotation = JAnnotationFactory.create(Generated.class, Map.of("value", value));
        assertImportData(Generated.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(value.getType(), "value"));
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));

        // Override one default
        annotation = JAnnotationFactory.create(Generated.class, Map.of("value", value, "date", date));
        assertImportData(Generated.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(value.getType(), "value"), new AnonymousMethod<>(date.getType(), "date"));
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
        Assertions.assertEquals(date, annotation.getValue(new AnonymousMethod<>(date.getType(), "date")));

        // Override all default
        annotation = JAnnotationFactory.create(Generated.class, Map.of("value", value, "date", date, "comments", comments));
        assertImportData(Generated.class, annotation);
        CollectionAssert.assertEquivalent(annotation.getAttributes(), new AnonymousMethod<>(value.getType(), "value"), new AnonymousMethod<>(date.getType(), "date"),
                new AnonymousMethod<>(comments.getType(), "comments"));
        Assertions.assertEquals(value, annotation.getValue(new AnonymousMethod<>(value.getType(), "value")));
        Assertions.assertEquals(date, annotation.getValue(new AnonymousMethod<>(date.getType(), "date")));
        Assertions.assertEquals(comments, annotation.getValue(new AnonymousMethod<>(comments.getType(), "comments")));
    }

    /**
     * Verify the annotation has the correct class type
     * 
     * @param expected {@link Class} that the annotation is expected to have
     * @param actual   {@link JAnnotation} that is to be verified
     */
    private void assertImportData(Class<?> expected, JAnnotation actual) {
        TendrilAssert.assertImportData(expected.getPackageName(), expected.getSimpleName(), actual.getType());
    }
}
