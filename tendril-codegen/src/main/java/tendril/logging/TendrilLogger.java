package tendril.logging;

import java.util.logging.Logger;

/**
 * Helper through which to retrieve pre-configured loggers for the different tendril libraries
 */
public class TendrilLogger {

	/**
	 * Helper to retrieve the logger for the tendril-codegen library
	 * 
	 * @return {@link Logger} for the codegen library
	 */
	public static Logger getCodegenLogger() {
		return Logger.getLogger("tendril.codegen");
	}

	/**
	 * Helper to retrieve the logger for the tendril-di library
	 * 
	 * @return {@link Logger} for the di library
	 */
	public static Logger getDiLogger() {
		return Logger.getLogger("tendril.di");
	}
}
