package tendril.codegen;

public enum PoDType {
    BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
