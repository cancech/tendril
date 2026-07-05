package tempApp.duplicate;

import tempApp.Message;
import tempApp.StaticBlueprint;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;

@Duplicate(StaticBlueprint.class)
@Singleton
@Named("StaticDuplicateBean")
public class StaticDuplicateBean {

	@Inject
	@Sibling
	StaticBlueprint blueprint;
	
	@Inject
	@Message
	String message;
	
	@Inject
	@Sibling
	StaticDuplicateBean2 bean2;
	
	private final StaticBlueprint ctorBlueprint;
	private final String ctorMsg;
	
	StaticDuplicateBean(@Sibling StaticBlueprint blueprint, @Message String msg) {
		ctorBlueprint = blueprint;
		ctorMsg = msg;
	}
	
	public StaticBlueprint getBlueprint() {
		return blueprint;
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
	
	public boolean isCorrectSibling() {
		return blueprint == bean2.getBlueprint();
	}
}
