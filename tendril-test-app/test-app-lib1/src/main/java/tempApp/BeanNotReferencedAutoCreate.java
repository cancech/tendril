package tempApp;

import tendril.bean.AutoCreate;
import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("This bean is not used but is auto-created")
@AutoCreate
public class BeanNotReferencedAutoCreate {

}
