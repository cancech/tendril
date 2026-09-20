package tempApp;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("This bean is not used")
public class BeanNotReferenced {

}
