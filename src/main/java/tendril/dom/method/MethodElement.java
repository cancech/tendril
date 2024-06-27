package tendril.dom.method;

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.field.type.TypeData;
import tendril.dom.type.NamedTypeElement;
import tendril.dom.type.Type;

/**
 * Representation of a method
 * 
 * @param <DATA_TYPE> {@link Type} that the method returns
 */
public class MethodElement<DATA_TYPE extends Type> extends NamedTypeElement<DATA_TYPE> {
    /** List of parameters that the method takes */
    private final List<NamedTypeElement<?>> parameters = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param returnType {@link TypeData} representing what the method returns
     * @param name       {@link String} the name of the method
     */
    public MethodElement(TypeData<DATA_TYPE> returnType, String name) {
        super(returnType, name);
    }

    /**
     * Add a parameter to the method. Parameters are expected to be added in the order they appear in the method.
     * 
     * @param parameter {@link NamedTypeElement} representing the method parameter
     */
    public void addParameter(NamedTypeElement<?> parameter) {
        parameters.add(parameter);
    }

    /**
     * Get all of the parameters of the method
     * 
     * @return {@link List} of {@link NamedTypeElement}s representing the parameters
     */
    public List<NamedTypeElement<?>> getParameters() {
        return parameters;
    }
}
