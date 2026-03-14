package tempApp;

import tendril.bean.duplicate.Blueprint;
import tendril.bean.duplicate.BlueprintDriver;

@Blueprint
public class ClassDuplicate implements BlueprintDriver {
	
	private final String name;
	
	public ClassDuplicate(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
