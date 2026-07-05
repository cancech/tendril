package tempApp.duplicate;

import tempApp.StaticBlueprint;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Configuration
public class StaticDuplicateConfiguration {

	@Duplicate(StaticBlueprint.class)
	@Singleton
	Printer staticPrinter(@Sibling StaticBlueprint details, @Sibling StaticDuplicateBean bean, @Sibling StaticDuplicateBean2 bean2, @Sibling StaticDuplicateBean3 bean3) {
		return () -> {
			System.out.println("StaticPrinter for " + details + " with siblings " + bean.getString() + ", " + bean2.getString() + ", " + bean3.getString());
		};
	}
}
