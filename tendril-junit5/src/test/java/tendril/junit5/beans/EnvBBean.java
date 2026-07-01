package tendril.junit5.beans;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.requirement.RequiresEnv;

@Bean
@Singleton
@RequiresEnv("B")
public class EnvBBean implements EnvBean {

}
