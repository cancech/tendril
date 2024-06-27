package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

public class JValueEnum<E extends Enum<E>> extends JValue<E> {

    JValueEnum(E value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassType> classImports) {
        classImports.add(new ClassType(value.getClass()));
        return value.getClass().getSimpleName() + "." + value.name();
    }
}