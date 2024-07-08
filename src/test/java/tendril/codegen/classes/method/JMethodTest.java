/**
 * 
 */
package tendril.codegen.classes.method;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.TypeData;
import tendril.dom.type.NamedTypeElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

/**
 * Test case for {@link JMethod}
 */
public class JMethodTest extends SharedJMethodTest {

    private static final String GENERATED_TEXT = "Generated_Method_Start ";

    /**
     * Concrete implementation to employ for testing
     */
    private class TestJMethod extends JMethod<Type> {
        /** Counter for the number of times that generateSignatureStart was called */
        private int timesGenerateCalled = 0;
        /** The last hasImplementation that was passed to generateSignatureStart */
        private boolean hasImplementation = false;

        /**
         * CTOR
         * 
         * @param implementation {@link List} of {@link String} lines of text for the implementation
         */
        protected TestJMethod(List<String> implementation) {
            super(mockVisibility, mockMethodElement, implementation);
        }

        /**
         * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
         */
        @Override
        protected String generateSignatureStart(boolean hasImplementation) {
            timesGenerateCalled++;
            this.hasImplementation = hasImplementation;
            return GENERATED_TEXT;
        }

        /**
         * Verify that generateSignatureStart was called appropriately
         * 
         * @param expectedTimes             int times expected to have been called
         * @param expectedHasImplementation boolean the expected hasImplementation from the last call
         */
        private void verifyGenerateCalled(int expectedTimes, boolean expectedHasImplementation) {
            Assertions.assertEquals(expectedTimes, timesGenerateCalled);
            Assertions.assertEquals(expectedHasImplementation, hasImplementation);
        }
    }

    // Mocks to use for testing
    @Mock
    private NamedTypeElement<Type> mockParam1;
    @Mock
    private TypeData<Type> mockParam1Type;
    @Mock
    private NamedTypeElement<Type> mockParam2;
    @Mock
    private TypeData<Type> mockParam2Type;
    @Mock
    private NamedTypeElement<Type> mockParam3;
    @Mock
    private TypeData<Type> mockParam3Type;

    // Instance to use for testing
    private TestJMethod method;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();

        lenient().when(mockParam1.getName()).thenReturn("mockParam1");
        lenient().when(mockParam2.getName()).thenReturn("mockParam2");
        lenient().when(mockParam3.getName()).thenReturn("mockParam3");

        lenient().when(mockParam1.getType()).thenReturn(mockParam1Type);
        lenient().when(mockParam2.getType()).thenReturn(mockParam2Type);
        lenient().when(mockParam3.getType()).thenReturn(mockParam3Type);

        lenient().when(mockParam1Type.getSimpleName()).thenReturn("Type1");
        lenient().when(mockParam2Type.getSimpleName()).thenReturn("Type2");
        lenient().when(mockParam3Type.getSimpleName()).thenReturn("Type3");
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     * @param code       {@link List} of {@link String} lines of code to use for the method implementation
     * @param parameters {@link List} of {@link NamedTypeElement}s that are be used as parameters for the method
     */
    private void initMethod(List<String> code, List<NamedTypeElement<?>> parameters) {
        when(mockMethodElement.getParameters()).thenReturn(parameters);
        method = new TestJMethod(code);
        verifyMethodInit(method);
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and no parameters.
     */
    @Test
    public void testNoImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName();");

        // Populate the method details
        initMethod(null, Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and a single parameter.
     */
    @Test
    public void testNoImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1);");

        // Populate the method details
        initMethod(null, Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is no implementation and several parameters.
     */
    @Test
    public void testNoImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3);");

        // Populate the method details
        initMethod(null, Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(false);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and no parameters.
     */
    @Test
    public void testEmptyImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName() {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and a single parameter.
     */
    @Test
    public void testEmptyImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is an empty implementation and several parameters.
     */
    @Test
    public void testEmptyImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3) {");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.emptyList(), Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and no parameters.
     */
    @Test
    public void testSomeImplementationNoParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName() {");
        matcher.eq("    qwerty");
        matcher.eq("}");

        // Populate the method details
        initMethod(Collections.singletonList("qwerty"), Collections.emptyList());

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and a single parameter.
     */
    @Test
    public void testSomeImplementationOneParameter() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1) {");
        matcher.eq("    qwerty");
        matcher.eq("    asdfg");
        matcher.eq("    zxcvb");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "asdfg", "zxcvb"), Collections.singletonList(mockParam1));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
    }

    /**
     * Verify that the method code is properly generated when there is some implementation and several parameters.
     */
    @Test
    public void testSomeImplementationSeveralParameters() {
        // What code is expected
        matcher.eq(GENERATED_TEXT + "Return_Type mockMethodName(Type1 mockParam1, Type2 mockParam2, Type3 mockParam3) {");
        matcher.eq("    qwerty");
        matcher.eq("    asdfg");
        matcher.eq("    zxcvb");
        matcher.eq("    hjhio");
        matcher.eq("    12345");
        matcher.eq("    inlknfginsdr934");
        matcher.eq("}");

        // Populate the method details
        initMethod(Arrays.asList("qwerty", "asdfg", "zxcvb", "hjhio", "12345", "inlknfginsdr934"), Arrays.asList(mockParam1, mockParam2, mockParam3));

        // Verify that it produces what is expected
        generateAndVerifyCode(true);
        verify(mockParam1).getType();
        verify(mockParam1).getName();
        verify(mockParam1Type).getSimpleName();
        verify(mockParam2).getType();
        verify(mockParam2).getName();
        verify(mockParam2Type).getSimpleName();
        verify(mockParam3).getType();
        verify(mockParam3).getName();
        verify(mockParam3Type).getSimpleName();
    }

    /**
     * Generate the code and verify that it matches expectations
     * 
     * @param expectedHasImplementation boolean true if generateSignatureStart is expected to be called with an implementation
     */
    private void generateAndVerifyCode(boolean expectedHasImplementation) {
        Set<ClassType> imports = new HashSet<ClassType>();
        method.generate(builder, imports);
        method.verifyGenerateCalled(1, expectedHasImplementation);
        verify(mockMethodElement, times(3)).getType();
        verify(mockReturnType).registerImport(imports);
        verify(mockReturnType).getSimpleName();
        verify(mockMethodElement).getParameters();

        matcher.match(builder.get());
    }
}
