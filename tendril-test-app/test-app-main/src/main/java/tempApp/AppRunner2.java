/*
 * Copyright 2025 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tempApp;

import java.util.List;
import java.util.Map;

import tempApp.duplicate.DynamicDuplicate;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresNotEnv;
import tendril.context.launch.Runner;

@Runner
@RequiresNotEnv("AppRunner1")
public class AppRunner2 extends AbstractAppRunner {

	private int timesVerifyDynamicDuplicatesCalled = 0;

	@Inject
	@Named("d")
	DynamicDuplicate dDup;
	@Inject
	@Named("e")
	DynamicDuplicate eDup;
	@Inject
	@Named("f")
	DynamicDuplicate fDup;
	@Inject
	@Named("g")
	DynamicDuplicate gDup;
	@Inject
	@Named("h")
	DynamicDuplicate hDup;
	@Inject
	@Named("i")
	DynamicDuplicate iDup;

	public AppRunner2() {
		super(AppRunner2.class, new DuplicationDetails("d", 321, 3.21), new DuplicationDetails("e", 432, 4.32), new DuplicationDetails("f", 543, 5.43), new DuplicationDetails("g", 654, 6.54),
				new DuplicationDetails("h", 765, 7.65), new DuplicationDetails("i", 876, 8.76));
	}

	@Inject
	void verifyDynamicDuplicateInjection(@InjectAll List<DynamicDuplicate> all, @Named("d") DynamicDuplicate d, @Named("e") DynamicDuplicate e, @Named("f") DynamicDuplicate f,
			@Named("g") DynamicDuplicate g, @Named("h") DynamicDuplicate h, @Named("i") DynamicDuplicate i) {
		timesVerifyDynamicDuplicatesCalled++;
	}

	@Override
	public void run() {
		super.run();
		assertion(timesVerifyDynamicDuplicatesCalled == 1, "verifyDynamicDuplicateInjection() was expected to be called once, but was called " + timesVerifyDynamicDuplicatesCalled + " times");
		assertDynamicDuplicatesInAll(Map.of("D", dDup, "E", eDup, "F", fDup, "G", gDup, "H", hDup, "I", iDup));
		System.out.println("Dynamic duplicates validated!");
	}
}
