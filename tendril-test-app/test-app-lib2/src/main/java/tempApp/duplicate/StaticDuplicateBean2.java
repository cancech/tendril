package tempApp.duplicate;

import tempApp.Message;
import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tendril.bean.Factory;
import tendril.bean.Inject;
import tendril.bean.duplicate.Sibling;

@StaticDuplicateBlueprint
@Factory
public class StaticDuplicateBean2 {

	@Inject
	@Message
	String message;
	
	private final StaticDuplicate blueprint;
	private final String ctorMsg;
	
	StaticDuplicateBean2(@Sibling StaticDuplicate blueprint, @Message String msg) {
		this.blueprint = blueprint;
		ctorMsg = msg;
	}
	
	public StaticDuplicate getBlueprint() {
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
