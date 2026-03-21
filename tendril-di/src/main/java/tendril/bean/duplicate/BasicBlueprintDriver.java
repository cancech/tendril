package tendril.bean.duplicate;

/**
 * The base {@link BlueprintDriver} implementation that can be reused by dynamic blueprints
 */
public class BasicBlueprintDriver implements BlueprintDriver {
	/** The name of the blueprint */
	private final String name;
	
	/**
	 * CTOR
	 * 
	 * @param name {@link String} the name of the driver
	 */
	public BasicBlueprintDriver(String name) {
		this.name = name;
	}

	/**
	 * @see tendril.bean.duplicate.BlueprintDriver#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

}
