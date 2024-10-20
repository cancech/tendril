package tempApp;

import tendril.bean.qualifier.BeanId;
import tendril.bean.qualifier.BeanIdEnum;

@BeanIdEnum
public enum MyType implements BeanId {
	VAL1, VAL2;
}
