package tempApp.duplicate;

import tempApp.Message;
import tempApp.StaticBlueprint;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;

@Duplicate(StaticBlueprint.class)
@Factory
public class StaticDuplicateBean2 {

	@Inject
	@Message
	String message;
	
	private final StaticBlueprint blueprint;
	private final String ctorMsg;
	
	StaticDuplicateBean2(@Sibling StaticBlueprint blueprint, @Message String msg) {
		this.blueprint = blueprint;
		ctorMsg = msg;
	}
	
	public StaticBlueprint getBlueprint() {
		return blueprint;
	}
	
	public int getInteger() {
		return blueprint.getInteger();
	}
	
	public double getDouble() {
		return blueprint.getDouble();
	}
	
	public String getString() {
		return blueprint.getString();
	}
	
	public boolean isSameMessage() {
		return message.equals(ctorMsg);
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isEquivalent(StaticDuplicateBean2 other) {
		return blueprint == other.blueprint && message.equals(other.message);
	}
}
