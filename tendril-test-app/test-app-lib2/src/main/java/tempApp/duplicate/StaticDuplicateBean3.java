package tempApp.duplicate;

import tempApp.Message;
import tempApp.StaticDuplicate;
import tempApp.StaticDuplicateBlueprint;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;

@StaticDuplicateBlueprint
@Singleton
public class StaticDuplicateBean3 {

	@Inject
	@Message
	String message;
	
	@Inject
	@Sibling
	StaticDuplicateBean bean;
	
	@Inject
	@Sibling
	StaticDuplicateBean2 bean2;
	
	private final StaticDuplicate blueprint;
	private final String ctorMsg;
	private final StaticDuplicateBean ctorBean;
	
	StaticDuplicateBean3(@Sibling StaticDuplicate blueprint, @Message String msg, @Sibling StaticDuplicateBean ctorBean) {
		this.blueprint = blueprint;
		ctorMsg = msg;
		this.ctorBean = ctorBean;
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
	
	public boolean isCorrectSibling() {
		return blueprint == bean.getBlueprint() && blueprint == bean2.getBlueprint();
	}
	
	public boolean isCorrectBean(StaticDuplicateBean expected) {
		return expected == bean && expected == ctorBean;
	}
}
