package tempApp;

import tendril.bean.Provider;
import tendril.bean.qualifier.Named;

public class TempManager {
    
    public String other;

	private final TempClass cls;
	
	public TempManager(TempClass cls) {
		this.cls = cls;
	}
	
	@Provider
	@MyTypeId(MyType.VAL2)
	public String doSomething(@TempEnum(val1="abc", val2=123) String value, @Named("OtherCls") TempClass otheCls, int abc) {
		return cls.getValue();
	}
}
