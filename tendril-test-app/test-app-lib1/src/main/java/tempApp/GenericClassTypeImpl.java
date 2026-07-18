package tempApp;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.qualifier.Named;

@Bean
@Singleton
@Named("GenericClassType")
public class GenericClassTypeImpl implements GenericClassType<String, String> {

}
