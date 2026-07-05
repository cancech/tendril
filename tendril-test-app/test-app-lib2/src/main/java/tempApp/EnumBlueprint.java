package tempApp;

import tendril.bean.duplicate.Blueprint;

public enum EnumBlueprint implements Blueprint {

	E1, E2, E3, E4, E5;

	@Override
	public String getName() {
		return name();
	}
	
}
