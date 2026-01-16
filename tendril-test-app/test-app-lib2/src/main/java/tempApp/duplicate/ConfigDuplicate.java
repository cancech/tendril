package tempApp.duplicate;

import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;

@Configuration
public class ConfigDuplicate {
	
	@Bean
	@Singleton
	@Named("ConfigDuplicateAbc123")
	String abc123() {
		return "abc123";
	}
	
	@StaticDuplicateBlueprint
	@Singleton
	StringInterface createStringInterfaceCopy(@Sibling StaticDuplicate copy, @MyTypeId(MyType.VAL1) String val, @Sibling StaticDuplicateBean bean, @Named("ConfigDuplicateAbc123") String abc123) {
		System.err.println(copy.getString() + " received: " + val + " " + abc123);
		System.err.println("My copy is: " + copy + " bean copy: " + bean.getBlueprint());
		if (copy != bean.getBlueprint())
			throw new AssertionError("Sibling bean has a different type of blueprint [I am " + copy + " but sibling bean is " + bean.getBlueprint() + "]");
		return () -> copy.getString();
	}
	
}
