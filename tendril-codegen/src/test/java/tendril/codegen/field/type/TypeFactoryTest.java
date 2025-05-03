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
package tendril.codegen.field.type;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.generics.GenericFactory;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;

/**
 * Test case for {@link TypeFactory}
 */
public class TypeFactoryTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private TypeMirror mockMirror;
    @Mock
    private DeclaredType mockDeclaredMirror;
    @Mock
    private Element mockElement;
    @Mock
    private javax.lang.model.type.ArrayType mockArrayMirror;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that a Void type can be created properly from {@link TypeKind}
     */
    @Test
    public void testCreateVoidFromTypeKind() {
        performCreateTest(TypeKind.VOID, VoidType.INSTANCE);
        verify(mockMirror).getKind();
    }

    /**
     * Verify that a primitive type can be created properly from {@link TypeKind}
     */
    @Test
    public void testCreatePrimitiveTypeKind() {
        int timesGetKind = 0;
        performCreateTest(TypeKind.BOOLEAN, PrimitiveType.BOOLEAN);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.BYTE, PrimitiveType.BYTE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.CHAR, PrimitiveType.CHAR);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.DOUBLE, PrimitiveType.DOUBLE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.FLOAT, PrimitiveType.FLOAT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.INT, PrimitiveType.INT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.LONG, PrimitiveType.LONG);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.SHORT, PrimitiveType.SHORT);
        verify(mockMirror, times(++timesGetKind)).getKind();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindNoGeneric() {
        setupDeclaredMirror("a.b.c.D");
        performCreateTest(mockDeclaredMirror, TypeKind.DECLARED, new ClassType("a.b.c.D"));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindSingleExplicitGeneric() {
        setupDeclaredMirror("a.b.c.D", createMockedDeclaredType("q.w.e.r.t.Y"));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create(new ClassType("q.w.e.r.t.Y")));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindSingleNamedGeneric() {
        setupDeclaredMirror("a.b.c.D", createMockedGenericType("T"));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create("T"));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindWithSingleExtendsGeneric() {
        setupDeclaredMirror("a.b.c.D", createMockedWildcardType("q.w.e.r.t.Y", true));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create(new ClassType("q.w.e.r.t.Y")));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindWithSingleSuperGeneric() {
        setupDeclaredMirror("a.b.c.D", createMockedWildcardType("q.w.e.r.t.Y", false));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create(new ClassType("q.w.e.r.t.Y")));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindWithSinglePureGeneric() {
        setupDeclaredMirror("a.b.c.D", createMockedWildcardType("", false));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create(new ClassType(Object.class)));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKindWithMultipleGenerics() {
        setupDeclaredMirror("a.b.c.D", createMockedWildcardType("q.w.e.r.t.Y", false), createMockedWildcardType("", false), createMockedWildcardType("a.s.d.f.G", true),
                createMockedDeclaredType("z.x.c.V"), createMockedGenericType("U"));

        Type createdType = TypeFactory.create(mockDeclaredMirror);
        ClassAssert.assertInstance(ClassType.class, createdType);

        ClassType created = (ClassType) createdType;
        Assertions.assertEquals("a.b.c.D", created.getFullyQualifiedName());
        CollectionAssert.assertEquivalent(created.getGenerics(), GenericFactory.create(new ClassType(Object.class)), GenericFactory.create(new ClassType("q.w.e.r.t.Y")),
                GenericFactory.create(new ClassType("a.s.d.f.G")), GenericFactory.create(new ClassType("z.x.c.V")), GenericFactory.create("U"));

        verify(mockDeclaredMirror).getKind();
        verify(mockDeclaredMirror).asElement();
        verify(mockDeclaredMirror).getTypeArguments();
    }

    /**
     * Verify that an array can be created properly from {@link TypeKind}
     */
    @Test
    public void testCreateArrayTypeKind() {
        when(mockArrayMirror.getKind()).thenReturn(TypeKind.ARRAY);
        when(mockArrayMirror.getComponentType()).thenReturn(mockMirror);
        when(mockMirror.getKind()).thenReturn(TypeKind.INT);
        Assertions.assertEquals(new ArrayType<Type>(PrimitiveType.INT), TypeFactory.create(mockArrayMirror));
        verify(mockArrayMirror).getKind();
        verify(mockArrayMirror).getComponentType();
        verify(mockMirror).getKind();
    }

    /**
     * Verify that an exception is thrown if creation from any other {@link TypeKind} is attempted
     */
    @Test
    public void testCreateOtherTypeKind() {
        int timesGetKind = 0;
        for (TypeKind kind : TypeKind.values()) {
            if (kind.isPrimitive() || kind == TypeKind.VOID || kind == TypeKind.DECLARED || kind == TypeKind.ARRAY || kind == TypeKind.WILDCARD || kind == TypeKind.TYPEVAR)
                continue;

            when(mockMirror.getKind()).thenReturn(kind);
            Assertions.assertThrows(DefinitionException.class, () -> TypeFactory.create(mockMirror));
            verify(mockMirror, times(++timesGetKind)).getKind();
        }
    }

    /**
     * Verify that the primitives can be created from the class
     */
    @Test
    public void testCreatePrimitiveFromClass() {
        performCreateTest(Boolean.class, PrimitiveType.BOOLEAN);
        performCreateTest(boolean.class, PrimitiveType.BOOLEAN);
        performCreateTest(Byte.class, PrimitiveType.BYTE);
        performCreateTest(byte.class, PrimitiveType.BYTE);
        performCreateTest(Character.class, PrimitiveType.CHAR);
        performCreateTest(char.class, PrimitiveType.CHAR);
        performCreateTest(Double.class, PrimitiveType.DOUBLE);
        performCreateTest(double.class, PrimitiveType.DOUBLE);
        performCreateTest(Float.class, PrimitiveType.FLOAT);
        performCreateTest(float.class, PrimitiveType.FLOAT);
        performCreateTest(Integer.class, PrimitiveType.INT);
        performCreateTest(int.class, PrimitiveType.INT);
        performCreateTest(Long.class, PrimitiveType.LONG);
        performCreateTest(long.class, PrimitiveType.LONG);
        performCreateTest(Short.class, PrimitiveType.SHORT);
        performCreateTest(short.class, PrimitiveType.SHORT);
    }

    /**
     * Verify that a {@link ClassType} can be created from an arbitrary {@link Class}
     */
    @Test
    public void createClassTypeFromClass() {
        performCreateTest(ClassType.class, new ClassType(ClassType.class));
    }

    /**
     * Verify that the appropriate Type is created from an Array
     */
    @Test
    public void createArrayFromClass() {
        performCreateTest(Boolean[].class, new ArrayType<PrimitiveType>(PrimitiveType.BOOLEAN));
        performCreateTest(boolean[].class, new ArrayType<PrimitiveType>(PrimitiveType.BOOLEAN));
        performCreateTest(Byte[].class, new ArrayType<PrimitiveType>(PrimitiveType.BYTE));
        performCreateTest(byte[].class, new ArrayType<PrimitiveType>(PrimitiveType.BYTE));
        performCreateTest(Character[].class, new ArrayType<PrimitiveType>(PrimitiveType.CHAR));
        performCreateTest(char[].class, new ArrayType<PrimitiveType>(PrimitiveType.CHAR));
        performCreateTest(Double[].class, new ArrayType<PrimitiveType>(PrimitiveType.DOUBLE));
        performCreateTest(double[].class, new ArrayType<PrimitiveType>(PrimitiveType.DOUBLE));
        performCreateTest(Float[].class, new ArrayType<PrimitiveType>(PrimitiveType.FLOAT));
        performCreateTest(float[].class, new ArrayType<PrimitiveType>(PrimitiveType.FLOAT));
        performCreateTest(Integer[].class, new ArrayType<PrimitiveType>(PrimitiveType.INT));
        performCreateTest(int[].class, new ArrayType<PrimitiveType>(PrimitiveType.INT));
        performCreateTest(Long[].class, new ArrayType<PrimitiveType>(PrimitiveType.LONG));
        performCreateTest(long[].class, new ArrayType<PrimitiveType>(PrimitiveType.LONG));
        performCreateTest(Short[].class, new ArrayType<PrimitiveType>(PrimitiveType.SHORT));
        performCreateTest(short[].class, new ArrayType<PrimitiveType>(PrimitiveType.SHORT));
        performCreateTest(ClassType[].class, new ArrayType<ClassType>(new ClassType(ClassType.class)));
    }

    /**
     * Setup the mockDeclaredMirror with the specified values so that it can be used in a test
     * 
     * @param elementName {@link String} the name of the element to return from toString().
     * @param generics    {@link TypeMirror}... vararg indicating all of the generic types that should be "applied" to the mockDeclaredMirror
     */
    private void setupDeclaredMirror(String elementName, TypeMirror... generics) {
        doReturn(Arrays.asList(generics)).when(mockDeclaredMirror).getTypeArguments();
        when(mockDeclaredMirror.asElement()).thenReturn(mockElement);
        when(mockElement.toString()).thenReturn(elementName);

        if (generics.length > 0)
            when(mockDeclaredMirror.getKind()).thenReturn(TypeKind.DECLARED);
    }

    /**
     * Helper for creating and defining a mocked {@link DeclaredType} which can be used as a generic argument for a mocked {@link DeclaredType}. Namely this is expected to be passed as a
     * {@link TypeMirror} parameter to {@code setupDeclaredMirrir()}.
     * 
     * @param name {@link String} the fully qualified name of the class to employ for the generic
     * @return mocked {@link DeclaredType}
     */
    private DeclaredType createMockedDeclaredType(String name) {
        DeclaredType mockGeneric = mock(DeclaredType.class);
        when(mockGeneric.getKind()).thenReturn(TypeKind.DECLARED);
        Element mockGenericElement = mock(Element.class);
        when(mockGenericElement.toString()).thenReturn(name);
        when(mockGeneric.asElement()).thenReturn(mockGenericElement);
        when(mockGeneric.getTypeArguments()).thenReturn(Collections.emptyList());
        return mockGeneric;
    }

    /**
     * Helper for creating and defining a mocked {@link WildcardType} which can be used as a generic argument for a mocked {@link DeclaredType}. Namely this is expected to be passed as a
     * {@link TypeMirror} parameter to {@code setupDeclaredMirror()}.
     * 
     * @param parentName {@link String} the fully qualified name of the parent to extends/super. Leave blank for pure wildcard (i.e.: just <?>)
     * @param isExtends  boolean true if {@code extends}, false if {@code super}. Irrelevant for pure wildcard (i.e.: just <?>)
     * @return mocked {@link WildcardType}
     */
    private WildcardType createMockedWildcardType(String parentName, boolean isExtends) {
        WildcardType mockGeneric = mock(WildcardType.class);
        when(mockGeneric.getKind()).thenReturn(TypeKind.WILDCARD);

        if (parentName == null || parentName.isBlank()) {
            when(mockGeneric.getExtendsBound()).thenReturn(null);
            when(mockGeneric.getSuperBound()).thenReturn(null);
        } else {
            DeclaredType mockParent = createMockedDeclaredType(parentName);
            if (isExtends) {
                when(mockGeneric.getExtendsBound()).thenReturn(mockParent);
            } else {
                when(mockGeneric.getExtendsBound()).thenReturn(null);
                when(mockGeneric.getSuperBound()).thenReturn(mockParent);
            }
        }

        return mockGeneric;
    }

    /**
     * Helper for creating and defining a mocked {@link TypeMirror} which can be used as a generic argument (i.e.: T) for a mocked {@link DeclaredType}. Namely this is expected to be passed as a
     * {@link TypeMirror} parameter to {@code setupDeclaredMirror()}.
     * 
     * @param typeName {@link String} the name to apply to the generic
     * @return mocked {@link TypeMirror}
     */
    private TypeMirror createMockedGenericType(String typeName) {
        TypeMirror mockGeneric = mock(TypeMirror.class);
        when(mockGeneric.getKind()).thenReturn(TypeKind.TYPEVAR);
        when(mockGeneric.toString()).thenReturn(typeName);
        return mockGeneric;
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirrorKind    {@link TypeKind} the mirror passed into the create method returns
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(TypeKind mirrorKind, Type expectCreated) {
        performCreateTest(mockMirror, mirrorKind, expectCreated);
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirror        {@link TypeMirror} the mocked mirror that is to be used for the test
     * @param mirrorKind    {@link TypeKind} the mirror passed into the create method returns
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(TypeMirror mirror, TypeKind mirrorKind, Type expectCreated) {
        when(mirror.getKind()).thenReturn(mirrorKind);
        Assertions.assertEquals(expectCreated, TypeFactory.create(mirror));
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirrorKind    {@link Class} defining the type that is to be created
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(Class<?> klass, Type expectCreated) {
        Assertions.assertEquals(expectCreated, TypeFactory.create(klass));
    }
}
