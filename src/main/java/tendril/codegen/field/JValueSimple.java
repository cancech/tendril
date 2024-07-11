/**
 * 
 */
package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Representation of simple values (plain ol' data types, {@link String}s), where the value is presented verbatim and at most some decoration (prefix, suffix).
 */
public class JValueSimple<T> extends JValue<T> {
    
    private final String prefix;
    private final String suffix;

    /**
     * @param value
     */
    public JValueSimple(T value) {
        this(value, "", "");
    }
    
    public JValueSimple(T value, String prefix, String suffix) {
        super(value);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        return prefix + value.toString() + suffix;
    }

}
