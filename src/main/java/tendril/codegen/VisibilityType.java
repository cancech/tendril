package tendril.codegen;

public enum VisibilityType {
    PUBLIC("public"),
    PRIVATE("private"),
    PACKAGE_PRIVATE(""),
    PROTECTED("protected");
    
    private final String code;
    
    private VisibilityType(String code) {
        this.code = code;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
