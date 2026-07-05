package tendril.bean.duplicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tendril.bean.Bean;
import tendril.bean.Configuration;

/**
 * To be applied to indicate that a class (or {@link Configuration} method) produces a bean that is duplicated. It is used in exactly the same manner as @{@link Bean}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Duplicate {

	/**
	 * Indicates the Blueprint which will provide the details of the duplicate, and as such drive duplication.
	 * 
	 * @return {@link Class} extending {@link Blueprint}
	 */
	Class<? extends Blueprint> value();
}
