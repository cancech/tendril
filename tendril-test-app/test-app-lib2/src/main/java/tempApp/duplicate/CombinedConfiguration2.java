package tempApp.duplicate;

import java.util.List;

import tempApp.ClassBlueprint;
import tempApp.DuplicationBlueprint;
import tempApp.EnumBlueprint;
import tempApp.Message;
import tempApp.StaticBlueprint;
import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.InjectAll;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;

@Configuration
public class CombinedConfiguration2 {

	@Duplicate(StaticBlueprint.class)
	@Singleton
	Printer staticPrinter(@Sibling StaticBlueprint details, @Sibling StaticDuplicateBean bean, @Sibling StaticDuplicateBean2 bean2, @Sibling StaticDuplicateBean3 bean3) {
		return () -> {
			System.out.println("Combined2 Printer for " + details + " with siblings " + bean.getString() + ", " + bean2.getString() + ", " + bean3.getString());
		};
	}

	@Duplicate(DuplicationBlueprint.class)
	@Singleton
	Printer dynamicPrinter(@InjectAll List<DynamicDuplicate> allDynamicDups, @Sibling DuplicationBlueprint details, @Sibling DynamicDuplicate siblingDup) {
		return () -> {
			System.out.println("Combined2 Printer for " + details.getName() + " - there are " + allDynamicDups.size() + " DynamicDuplicates and my sibling is " + siblingDup.getName());
		};
	}

	@Duplicate(EnumBlueprint.class)
	@Singleton
	Printer enumPrinter(@Sibling EnumBlueprint details) {
		return () -> {
			System.out.println("Combined2 Enum Printer for " + details);
		};
	}

	@Duplicate(ClassBlueprint.class)
	@Singleton
	Printer classPrinter(@Sibling ClassBlueprint details) {
		return () -> {
			System.out.println("Combined2 Class Printer for " + details);
		};
	}
	
	@Bean
	@Singleton
	Printer bean1Copy1Printer(@Named("COPY_1") StaticDuplicateBean bean1Copy1) {
		return () -> {
			System.out.println("Combined2 Printer for Copy 1 of StaticDuplicateBean: " + bean1Copy1.getString());
		};
	}
	
	@Bean
	@Singleton
	Printer messagePrinter(@Message String message, @MyTypeId(MyType.VAL1) String val1, @MyTypeId(MyType.VAL2) Object val2) {
		return () -> {
			System.out.println("Other Dynamic Printer MESSAGE: " + message);
			System.out.println("Other Dynamic Printer VAL1: " + val1);
			System.out.println("Other Dynamic Printer VAL2: " + val2);
		};
	}
	
	@Bean
	@Singleton
	Printer stringIFacePrinter(@InjectAll List<StringInterface> strIfaces) {
		return () -> {
			for (StringInterface iface: strIfaces)
				System.out.println("Other Dynamic Printer IFACE: " + iface.getValue());
		};
	}
}
