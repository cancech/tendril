package tendril.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for {@link Collection}s.
 */
public abstract class CollectionUtil {

	/**
	 * Hidden CTOR as no instances should be created
	 */
	private CollectionUtil() {
	}

	/**
	 * Check if the two collections have the same number of elements
	 * 
	 * @param lhs {@link Collection} of arbitrary elements on the left side of the comparison
	 * @param rhs {@link Collection} of arbitrary elements on the right side of the comparison
	 * @return boolean true if the two collections have the same number of elements
	 */
	public static boolean sameSize(Collection<?> lhs, Collection<?> rhs) {
		return lhs.size() == rhs.size();
	}
	
	/**
	 * Check if the two collections are equivalent (i.e.: have the same contents but not necessarily in the same order). For more than a surface level
	 * (i.e.: memory location) comparison, the element type in the collections must support equals comparison.
	 * 
	 * @param <T> the type of element stored within the {@link Collection}s
	 * @param lhs {@link Collection} of elements on the left side of the comparison
	 * @param rhs {@link Collection} of elements on the right side of the comparison
	 * @return boolean {@code true} if the collections are deemed to be equivalent
	 */
	public static <T> boolean equivalent(Collection<T> lhs, Collection<T> rhs) {
		if (!sameSize(lhs, rhs))
			return false;
		
		List<T> left = new ArrayList<>(lhs);
		for (T t: rhs) {
			if (!left.remove(t))
				return false;
		}
		
		return left.isEmpty();
	}
}
