package tendril.codegen.field;

import java.util.List;
import java.util.Set;

import tendril.dom.type.core.ClassType;

public class JValueArray<TYPE> extends JValue<List<JValue<TYPE>>> {

    protected JValueArray(List<JValue<TYPE>> values) {
        super(values);
    }

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
