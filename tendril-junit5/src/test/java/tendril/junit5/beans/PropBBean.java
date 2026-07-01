package tendril.junit5.beans;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.requirement.RequiresProp;

@Bean
@Singleton
@RequiresProp("B")
public class PropBBean implements TestBean {

}
