package tendril.codegen;

/**
 * Enumeration of the different visibilities that can be employed in the code
 */
public enum VisibilityType {
    PUBLIC("public"),
    PRIVATE("private"),
    PACKAGE_PRIVATE(""),
    PROTECTED("protected");
    
	/** The representation of the visibility type as code */
    private final String code;
    
    /**
     * CTOR
     * 
     * @param code {@link String} how the visibility is represented in code
     */
    private VisibilityType(String code) {
        this.code = code;
    }
    
    /**
     * Converts the "visibility" to code
     */
    @Override
    public String toString() {
        return code;
    }
}
