package tendril.bean.duplicate;

/**
 * The base {@link Blueprint} implementation that can be reused when defining client blueprints
 */
public class BasicBlueprint implements Blueprint {
	/** The name of the blueprint */
	private final String name;
	
	/**
	 * CTOR
	 * 
	 * @param name {@link String} the name of the driver
	 */
	public BasicBlueprint(String name) {
		this.name = name;
	}

	/**
	 * @see tendril.bean.duplicate.Blueprint#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

}
