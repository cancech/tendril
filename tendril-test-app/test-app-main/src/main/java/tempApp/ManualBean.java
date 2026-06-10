package tempApp;

public class ManualBean {

	private final int value;
	
	public ManualBean(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ManualBean other)
			return value == other.value;
		
		return false;
	}
}
