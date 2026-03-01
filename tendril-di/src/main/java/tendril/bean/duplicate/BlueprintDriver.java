package tendril.bean.duplicate;

/**
 * Must be applied to the {@link Blueprint} annotated class, as at a bare minimum it is expected and required for the blueprint to provide a name for each {@link Sibling}. This is the counterpoint to
 * the {@code name()} which is made available by {@link Enum} blueprints.
 */
public interface BlueprintDriver {

	// TODO expand test to validate usage in methods, parameters, and the like
	// TODO allow for blueprint class inheritance to be used.

	/**
	 * Get the name for the {@link Sibling}. Note that the {@code Name} <b>must be unique</b> for a given {@link BlueprintDriver} implementation. This means that every class that implements
	 * {@link BlueprintDriver} (whether directly or indirectly) must supply a unique name within the context of that class. What this means in practice is that while different {@link BlueprintDriver}s
	 * can reuse names, errors will be thrown if it is reused within the same {@link BlueprintDriver} class.
	 * 
	 * @return {@link String} name of the {@link Sibling}
	 */
	String getName();
}
