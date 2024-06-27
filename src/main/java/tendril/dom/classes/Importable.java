package tendril.dom.classes;

import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Indicates that a construct contains a reference to an element which needs to be imported when used in a class/code.
 */
public interface Importable {

    /**
     * Add the {@link ClassType} of what needs to be imported to the {@link Set} of all imports for the enclosing class
     * 
     * @param classImports {@link Set} of {@link ClassType} where all imports for the enclosing class are stored
     */
    void registerImport(Set<ClassType> classImports);
}
