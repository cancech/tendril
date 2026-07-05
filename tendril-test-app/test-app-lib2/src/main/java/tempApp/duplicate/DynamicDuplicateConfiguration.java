package tempApp.duplicate;

import java.util.List;

import tempApp.DuplicationBlueprint;
import tendril.bean.Configuration;
import tendril.bean.InjectAll;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Configuration
public class DynamicDuplicateConfiguration {

	@Duplicate(DuplicationBlueprint.class)
	@Singleton
	Printer dynamicPrinter(@InjectAll List<DynamicDuplicate> allDynamicDups, @Sibling DuplicationBlueprint details, @Sibling DynamicDuplicate siblingDup) {
		return () -> {
			System.out.println("DynamicPrinter for " + details.getName() + " - there are " + allDynamicDups.size() + " DynamicDuplicates and my sibling is " + siblingDup.getName());
		};
	}
}
