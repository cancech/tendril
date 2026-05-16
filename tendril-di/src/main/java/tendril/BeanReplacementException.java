package tendril;

/**
 * Exception that is thrown when an attempt to replace an existing bean fails.
 */
public class BeanReplacementException extends RuntimeException {

	/** Serial ID */
	private static final long serialVersionUID = -2745038853742553457L;

	/**
	 * CTOR
	 * 
	 * @param message {@link String} the reason for the failure
	 */
	public BeanReplacementException(String message) {
		super(message);
	}

	/**
	 * CTOR
	 * 
	 * @param message {@link String} the reason for the failure
	 * @param cause   {@link Throwable} which caused the failure
	 */
	public BeanReplacementException(String message, Throwable cause) {
		super(message, cause);
	}
}
