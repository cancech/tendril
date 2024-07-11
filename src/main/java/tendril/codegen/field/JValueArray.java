package tendril.codegen.field;

import java.util.List;
import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Representation of an array value
 * 
 * @param <TYPE> the type of element to be stored in the array
 */
public class JValueArray<TYPE> extends JValue<List<JValue<TYPE>>> {

    /**
     * CTOR
     * 
     * @param values {@link List} of {@link JValue}s representing all of the elements to appear in the array
     */
    protected JValueArray(List<JValue<TYPE>> values) {
        super(values);
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        String result = "{";
        for (int i = 0; i < value.size(); i++) {
            result += value.get(i).generate(classImports);
            if (i < value.size() - 1)
                result += ", ";
        }

        return result + "}";
    }
}
