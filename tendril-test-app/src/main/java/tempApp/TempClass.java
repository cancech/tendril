package tempApp;

import tendril.bean.Bean;

@Bean
@MyTypeId(MyType.VAL1)
public class TempClass {

	//private final String val;
	
//	public TempClass(String val) {
//		this.val = val;
//	}
//	
//	public String getValue() {
//		return val;
//	}
	
	@Override
	public String toString() {
		return "TempClass Value = TBD";// + val;
	}
}
