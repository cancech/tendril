package tendril.dom.classes;

/**
 * The representation of an importable (declared) element, containing the necessary details for importing it (namely package and class name).
 */
public class ImportElement {
    /** The name of the package where the importable item lives */
    private final String packageName;
    /** The name of the importable element (class or equivalent) */
    private final String className;

    /**
     * CTOR
     * 
     * @param klass {@link Class} from which to generate the importable element data
     */
    public ImportElement(Class<?> klass) {
        this(klass.getPackageName(), klass.getSimpleName());
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the element whose import data is to be generated
     */
    public ImportElement(String fullyQualifiedName) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot <= 0)
            throw new IllegalArgumentException("Invalid fully qualified class \"" + fullyQualifiedName + "\". Hint: default package is not supported");

        this.packageName = fullyQualifiedName.substring(0, lastDot);
        this.className = fullyQualifiedName.substring(lastDot + 1);
    }

    /**
     * CTOR
     * 
     * @param packageName {@link String} the name of the package where the importable element lives
     * @param className {@link String} the name of the importable element (class)
     */
    public ImportElement(String packageName, String className) {
        if (packageName == null || packageName.isBlank())
            throw new IllegalArgumentException("Invalid package \"" + packageName + "\" - valid (non default) package is required");

        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Get the name of the package
     * 
     * @return {@link String} name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Get the name of the class (or equivalent)
     * 
     * @return {@link String} name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Get the fully qualified name of the class (or equivalent)
     * 
     * @return {@link String} fully qualified name
     */
    public String getFullyQualifiedName() {
        return packageName + "." + className;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return packageName.hashCode() + className.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImportElement))
            return false;

        ImportElement other = (ImportElement) obj;
        return this.packageName.equals(other.packageName) && this.className.equals(other.className);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
