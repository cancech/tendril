package tendril.metadata.classes;

public class ImportData {
    
    private final String packageName;
    private final String className;

    public ImportData(Class<?> klass) {
        this(klass.getPackageName(), klass.getSimpleName());
    }

    public ImportData(String fullyQualifiedName) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot > 0) {
            this.packageName = fullyQualifiedName.substring(0, lastDot);
            this.className = fullyQualifiedName.substring(lastDot + 1);
        } else {
            this.packageName = "";
            this.className = fullyQualifiedName;
        }
    }

    public ImportData(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getFullyQualifiedName() {
        if (packageName == null || packageName.isBlank())
            return className;

        return packageName + "." + className;
    }

    @Override
    public int hashCode() {
        return packageName.hashCode() + className.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImportData))
            return false;

        ImportData other = (ImportData) obj;
        return this.packageName.equals(other.packageName) && this.className.equals(other.className);
    }
    
    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
