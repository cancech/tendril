package tempApp;

import tendril.codegen.field.type.PrimitiveType;

public class GenericWrapper<T> {

	private final T value;
	
	public GenericWrapper(T value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "GenericWrapper for " + value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GenericWrapper w)
			return value.equals(w.value);
		if (obj instanceof PrimitiveType t)
			return value == t;
		
		return super.equals(obj);
	}
}
