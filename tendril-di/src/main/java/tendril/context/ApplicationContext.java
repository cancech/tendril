package tendril.context;

import java.util.List;

import tendril.BeanRetrievalException;
import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.bean.qualifier.Descriptor;
import tendril.context.launch.TendrilRunner;

public interface ApplicationContext {
    /**
     * Start the context and trigger execution via the defined {@link TendrilRunner}
     */
	void start();

	/**
	 * Get the bean matching the provided descriptor. The descriptor must resolve to exactly one instance otherwise an exception will be thrown, though resolution is done on a priority basis:
	 * <ol>
	 * <li>Any {@link Primary} beans that match are attempted first</li>
	 * <li>Any basic (no explicit type) beans that match are attempted second</li>
	 * <li>Any {@link Fallback} beans are attempted only if none of the above types result in any matches</li>
	 * </ol>
	 * 
	 * With the priority in play, it is possible to find one explicit match even when there are multiple matches, so long as there is only a single match at the highest available priority level and
	 * all other matches are in lower levels. For example: a single {@link Primary} match will be returned regardless of how many basic or {@link Fallback} matches are present. If there is no
	 * {@link Primary} match, then the single basic will be returned, regardless of how many {@link Fallback} beans are present. If there are no {@link Primary} or basic beans, then there must be a
	 * single {@link Fallback} bean in the results. Multiple results in the highest available type will result in a {@link BeanRetrievalException} being thrown, as will be the case if there are no
	 * results at any level available.
	 * 
	 * @param <BEAN_TYPE> indicating the type of bean that is to be retrieved
	 * @param descriptor  {@link Descriptor} containing the description of the bean that is to be retrieved
	 * 
	 * @return The specific bean that is desired
	 * @throws BeanRetrievalException if there is an issue retrieving the desired bean
	 */
	<BEAN_TYPE> BEAN_TYPE getBean(Descriptor<BEAN_TYPE> descriptor);
	
	/**
	 * Get all beans that match the provided descriptor. The {@link List} can be empty if there are no matches. All matching {@link Primary} and basic (no explicit type) beans will be returned,
	 * {@link Fallback} beans will only be included if there are no {@link Primary} or basic matches.
	 * 
	 * @param <BEAN_TYPE> indicating the type of the beans that are to be retrieved
	 * @param descriptor  {@link Descriptor} containing the description of the beans that are to be retrieved
	 * @return {@link List} of matching beans
	 */
	<BEAN_TYPE> List<BEAN_TYPE> getAllBeans(Descriptor<BEAN_TYPE> descriptor);
}
