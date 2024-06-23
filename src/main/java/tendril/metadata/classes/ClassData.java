package tendril.metadata.classes;

public class ClassData extends ImportData {

    public ClassData(Class<?> klass) {
        super(klass);
    }

    public ClassData(String fullyQualifiedName) {
        super(fullyQualifiedName);
    }

    public ClassData(String packageName, String className) {
        super(packageName, className);
    }

    public ClassData newClassWithNameSuffix(String classSuffix) {
        return new ClassData(getPackageName(), getClassName() + classSuffix);
    }
}
