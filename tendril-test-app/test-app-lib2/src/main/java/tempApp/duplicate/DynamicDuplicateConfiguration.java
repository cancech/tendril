package tempApp.duplicate;

import java.util.List;

import tempApp.DuplicationDetails;
import tempApp.DuplicationDetailsBlueprint;
import tendril.bean.Configuration;
import tendril.bean.InjectAll;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;

@Configuration
public class DynamicDuplicateConfiguration {

	@DuplicationDetailsBlueprint
	@Singleton
	Printer dynamicPrinter(@InjectAll List<DynamicDuplicate> allDynamicDups, @Sibling DuplicationDetails details, @Sibling DynamicDuplicate siblingDup) {
		return () -> {
			System.out.println("DynamicPrinter for " + details.getName() + " - there are " + allDynamicDups.size() + " DynamicDuplicates and my sibling is " + siblingDup.getName());
		};
	}
	
	// TODO add test with dynamic and static in the same config
}
