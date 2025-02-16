package tempApp;

import tendril.bean.Bean;
import tendril.bean.qualifier.Named;

public class TempManager {
    
    public String other;

	private final SingletonClass cls;
	
	public TempManager(SingletonClass cls) {
		this.cls = cls;
	}
	
//	@Provider
//	@MyTypeId(MyType.VAL2)
//	public String doSomething(@TempEnum(val1="abc", val2=123) String value, @Named("OtherCls") TempClass otheCls, int abc) {
//		return cls.getValue();
//	}
}
