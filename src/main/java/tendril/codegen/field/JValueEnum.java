package tendril.codegen.field;

import java.util.Set;

import tendril.metadata.ClassData;

public class JValueEnum<E extends Enum<E>> extends JValue<E> {

    JValueEnum(E value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassData> classImports) {
        classImports.add(new ClassData(value.getClass()));
        return value.getClass().getSimpleName() + "." + value.name();
    }
}