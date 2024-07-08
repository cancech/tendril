package tendril.codegen.classes.method;

import java.util.List;

import tendril.codegen.VisibilityType;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

/**
 * Representation of a method that appears in an interface
 * 
 * @param <RETURN_TYPE> the {@link Type} that the method returns
 */
public class JMethodInterface<RETURN_TYPE extends Type> extends JMethod<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param visibility     {@link VisibilityType} indicating the desired visibility of the method
     * @param methodData     {@link MethodElement} with the basic metadata of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    public JMethodInterface(VisibilityType visibility, MethodElement<RETURN_TYPE> methodData, List<String> implementation) {
        super(visibility, methodData, implementation);
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
     */
    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        if (VisibilityType.PUBLIC == visibility)
            return hasImplementation ? "default " : "";

        return visibility.toString() + " ";
    }

}
