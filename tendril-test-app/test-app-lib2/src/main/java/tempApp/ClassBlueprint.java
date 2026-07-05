package tempApp;

import tendril.bean.duplicate.Blueprint;

public class ClassBlueprint implements Blueprint {
	
	private final String name;
	
	public ClassBlueprint(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
