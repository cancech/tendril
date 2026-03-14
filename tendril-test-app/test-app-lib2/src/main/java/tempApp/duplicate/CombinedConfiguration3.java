package tempApp.duplicate;

import java.util.List;

import tempApp.COPY_1;
import tempApp.ClassDuplicate;
import tempApp.ClassDuplicateBlueprint;
import tempApp.DuplicationDetails;
import tempApp.DuplicationDetailsBlueprint;
import tempApp.EnumDuplicate;
import tempApp.EnumDuplicateBlueprint;
import tempApp.Message;
import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tempApp.id.MyType;
import tempApp.id.MyTypeId;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.InjectAll;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;

@Configuration
public class CombinedConfiguration3 {

	@ClassDuplicateBlueprint
	@Singleton
	Printer classPrinter(@Sibling ClassDuplicate details) {
		return () -> {
			System.out.println("Combined2 Class Printer for " + details);
		};
	}

	@EnumDuplicateBlueprint
	@Singleton
	Printer enumPrinter(@Sibling EnumDuplicate details) {
		return () -> {
			System.out.println("Combined3 Enum Printer for " + details);
		};
	}

	@StaticDuplicateBlueprint
	@Singleton
	Printer staticPrinter(@Sibling StaticDuplicate details, @Sibling StaticDuplicateBean bean, @Sibling StaticDuplicateBean2 bean2, @Sibling StaticDuplicateBean3 bean3) {
		return () -> {
			System.out.println("Combined3 Printer for " + details + " with siblings " + bean.getString() + ", " + bean2.getString() + ", " + bean3.getString());
		};
	}

	@DuplicationDetailsBlueprint
	@Singleton
	Printer dynamicPrinter(@InjectAll List<DynamicDuplicate> allDynamicDups, @Sibling DuplicationDetails details, @Sibling DynamicDuplicate siblingDup) {
		return () -> {
			System.out.println("Combined3 Printer for " + details.getName() + " - there are " + allDynamicDups.size() + " DynamicDuplicates and my sibling is " + siblingDup.getName());
		};
	}
	
	@Bean
	@Singleton
	Printer bean1Copy1Printer(@COPY_1 StaticDuplicateBean bean1Copy1) {
		return () -> {
			System.out.println("Combined3 Printer for Copy 1 of StaticDuplicateBean: " + bean1Copy1.getString());
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
