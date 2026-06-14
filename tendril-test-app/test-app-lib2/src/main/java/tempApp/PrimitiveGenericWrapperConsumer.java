package tempApp;

import java.util.List;

import tendril.bean.Bean;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.Singleton;
import tendril.codegen.field.type.PrimitiveType;

@Bean
@Singleton
public class PrimitiveGenericWrapperConsumer {

	private final List<GenericWrapper<PrimitiveType>> wrapper;
	
	@Inject
	public PrimitiveGenericWrapperConsumer(List<GenericWrapper<PrimitiveType>> wrapper) {
		this.wrapper = wrapper;
	}
	
	@PostConstruct
	void process() {
		System.out.println("********************************************************");
		System.out.println("PrimitiveGenericWrapperConsumer");
		System.out.println("--------------------------------------------------------");
		for (GenericWrapper<PrimitiveType> w: wrapper)
			System.out.println(w.toString());
		System.out.println("********************************************************");
	}
}
