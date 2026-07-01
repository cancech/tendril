package tendril.junit5.beans;

import tendril.bean.Bean;
import tendril.bean.Singleton;
import tendril.bean.requirement.RequiresProp;

@Bean
@Singleton
@RequiresProp("A")
public class PropABean implements TestBean {

}
