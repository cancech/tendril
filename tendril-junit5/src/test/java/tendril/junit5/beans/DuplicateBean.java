package tendril.junit5.beans;

import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

/**
 * Dummy class to use for the purpose of validating bean duplication in a test
 */
@Duplicate(TestBlueprint.class)
@Singleton
public class DuplicateBean {

	/** Name applied to the duplicate */
	private final String name;

	/**
	 * CTOR for injection
	 * 
	 * @param blueprint {@link TestBlueprint} which drives the duplication
	 */
	@Inject
	public DuplicateBean(@Sibling TestBlueprint blueprint) {
		this(blueprint.getName());
	}

	/**
	 * CTOR
	 * 
	 * @param name {@link String} applied to the duplicate
	 */
	public DuplicateBean(String name) {
		this.name = name;
	}

	/**
	 * Get the name applied to the duplicate.
	 * 
	 * @return {@link String}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DuplicateBean b)
			return name.equals(b.name);

		return false;
	}
}
