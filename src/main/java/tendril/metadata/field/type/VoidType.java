package tendril.metadata.field.type;

public class VoidType {
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    @Override
    public String toString() {
        return "void";
    }
}
