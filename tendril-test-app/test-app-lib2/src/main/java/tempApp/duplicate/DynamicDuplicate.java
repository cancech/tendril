package tempApp.duplicate;

import tempApp.COPY_1;
import tempApp.COPY_2;
import tempApp.COPY_3;
import tempApp.DuplicationDetails;
import tempApp.DuplicationDetailsBlueprint;
import tempApp.lib1dup.ParentDuplicate;
import tendril.bean.Inject;
import tendril.bean.Singleton;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;

@DuplicationDetailsBlueprint
@Singleton
@Named("Generates a warning")
public class DynamicDuplicate {
	
	private final DuplicationDetails ctorDetails;
	private final StaticDuplicateBean bean1;
	private final StaticDuplicateBean2 bean2;
	private final StaticDuplicateBean3 bean3;
	
	@Inject
	@Sibling
	@Named("This be a warning")
	DuplicationDetails details;
	
	@Inject
	@Sibling
	ParentDuplicate parentDuplicate;

	@Inject
	public DynamicDuplicate(@Sibling DuplicationDetails details, @COPY_1 StaticDuplicateBean bean1, @COPY_2 StaticDuplicateBean2 bean2, @COPY_3 StaticDuplicateBean3 bean3) {
		this.ctorDetails = details;
		this.bean1 = bean1;
		this.bean2 = bean2;
		this.bean3 = bean3;
	}
	
	public DuplicationDetails getBlueprint() {
		return details;
	}
	
	public boolean isSameBlueprint() {
		return ctorDetails == details && parentDuplicate.isSameBlueprint(ctorDetails);
	}
	
	public String getName() {
		return ctorDetails.getName();
	}
	
	public int getInt() {
		return ctorDetails.getInt();
	}
	
	public double getDouble() {
		return ctorDetails.getDouble();
	}
	
	public StaticDuplicateBean getBean1() {
		return bean1;
	}
	
	public StaticDuplicateBean2 getBean2() {
		return bean2;
	}
	
	public StaticDuplicateBean3 getBean3() {
		return bean3;
	}
}
