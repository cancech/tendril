package tempApp.duplicate;

import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tendril.bean.Configuration;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;

@Configuration
public class StaticDuplicateConfiguration {

	@StaticDuplicateBlueprint
	@Singleton
	Printer staticPrinter(@Sibling StaticDuplicate details, @Sibling StaticDuplicateBean bean, @Sibling StaticDuplicateBean2 bean2, @Sibling StaticDuplicateBean3 bean3) {
		return () -> {
			System.out.println("StaticPrinter for " + details + " with siblings " + bean.getString() + ", " + bean2.getString() + ", " + bean3.getString());
		};
	}
}
