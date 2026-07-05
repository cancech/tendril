package tendril.bean.duplicate;

/**
 * Interface which "marks" a class as one which will provide the details of duplication and thus drive the creation of duplicates. At a bare minimum a name must be provided (as that is required by
 * {@code Tendril}), however when implemented/extended any number of additional parameters/fields can be provided. The bean which is to be duplicated must be annotated with @{@link Duplicate} and
 * the specific/concrete blueprint class indicated. The {@code Blueprint} instance which triggered the creation of the duplicate can be injected into the duplicate instance via @{@link Sibling} 
 */
public interface Blueprint {

	/**
	 * Get the name for the {@link Sibling}. Note that the {@code Name} <b>must be unique</b> for a given {@code Blueprint} implementation. This means that every class that implements
	 * {@code Blueprint} (whether directly or indirectly) must supply a unique name within the context of that class. What this means in practice is that while different {@code Blueprints} can reuse
	 * names, errors will be thrown if it is reused within the same {@code Blueprint} class.
	 * 
	 * @return {@link String} name of the {@link Sibling}
	 */
	String getName();
}
