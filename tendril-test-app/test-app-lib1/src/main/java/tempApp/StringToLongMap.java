package tempApp;

import tendril.bean.Bean;
import tendril.bean.Singleton;

@Bean
@Singleton
public class StringToLongMap extends StringToGenericMap<Long> {
	private static final long serialVersionUID = -1226439939015656075L;

	
	public StringToLongMap() {
		for (long i = 0; i < 100; i++)
			put(String.valueOf(i), i);
	}
}
