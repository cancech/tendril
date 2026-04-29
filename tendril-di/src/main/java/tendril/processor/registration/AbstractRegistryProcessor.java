package tendril.processor.registration;

import java.util.ArrayList;
import java.util.List;

import tendril.annotationprocessor.AbstractTendrilProccessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;

/**
 * Abstract processor for annotations from the registration family of annotations. The subclass must associate itself with the appropriate annotation but the end result is to assemble a registration
 * list of all annotated classes.
 */
abstract class AbstractRegistryProcessor extends AbstractTendrilProccessor {
	/** List of all recipes that are to be registered */
	private final List<String> registers = new ArrayList<>();
	/** The plain English name of the type of registration that is being collected */
	private final String type;
	/** The path to the file where the registration list is to be written */
	private final String path;

	/**
	 * CTOR
	 */
	public AbstractRegistryProcessor(String type, String path) {
		this.type = type;
		this.path = path;
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
	 */
	@Override
	protected ClassDefinition processType() {
		registers.add(currentClassType.getFullyQualifiedName());
		return null;
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
	 */
	@Override
	protected ClassDefinition processMethod() throws TendrilException {
		throw new InvalidConfigurationException(currentMethod.getFullElementPath() + " - " + type + " cannot be a method");
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#errorRaised()
	 */
	@Override
	protected void errorRaised() {
		processingOver();
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processingOver()
	 */
	@Override
	protected void processingOver() {
		super.processingOver();
		writeResourceFile(path, registers);
	}

}
