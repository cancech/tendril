package tendril.context;

/**
 * Debug utility which provides some features to support the debugging of beans in the application context.
 */
public interface BeanDebugger {

	/**
	 * Prints the list of beans which have not been initialized. This includes beans whose requirements have not been met,
	 * as well as those beans whose requirements have been met but which have not been referenced (and thus were not created).
	 */
	void printBeansNotCreated();
}
