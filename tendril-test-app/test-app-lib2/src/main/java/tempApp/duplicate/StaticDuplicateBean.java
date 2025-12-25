package tempApp.duplicate;

import tempApp.Message;
import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;

@StaticDuplicateBlueprint
@Singleton
public class StaticDuplicateBean {

	@Inject
	@Sibling
	StaticDuplicate blueprint;
	
	@Inject
	@Message
	String message;
	
	private final StaticDuplicate ctorBlueprint;
	private final String ctorMsg;
	
	StaticDuplicateBean(@Sibling StaticDuplicate blueprint, @Message String msg) {
		ctorBlueprint = blueprint;
		ctorMsg = msg;
	}
	
	public boolean isSameBlueprint() {
		return ctorBlueprint == blueprint;
	}
	
	public int getInteger() {
		return ctorBlueprint.getInteger();
	}
	
	public double getDouble() {
		return ctorBlueprint.getDouble();
	}
	
	public String getString() {
		return ctorBlueprint.getString();
	}
	
	public boolean isSameMessage() {
		return message.equals(ctorMsg);
	}
	
	public String getMessage() {
		return message;
	}
}
