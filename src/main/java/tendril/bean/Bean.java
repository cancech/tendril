package tendril.bean;

public interface Bean {
	default String getName() {
		return getClass().getName() + "-" + toString();
	}
}
