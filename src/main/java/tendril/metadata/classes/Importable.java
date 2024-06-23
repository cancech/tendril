package tendril.metadata.classes;

import java.util.Set;

public interface Importable {

    void registerImport(Set<ClassData> classImports);
}
