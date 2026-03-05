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
import tendril.bean.requirement.RequiresEnv;
import tendril.context.launch.Runner;

@Runner
@RequiresEnv("AppRunner1")
public class AppRunner1 extends AbstractAppRunner {

	private int timesVerifyDynamicDuplicatesCalled = 0;

	@Inject
	@Named("a")
	DynamicDuplicate aDup;
	@Inject
	@Named("b")
	DynamicDuplicate bDup;
	@Inject
	@Named("c")
	DynamicDuplicate cDup;

	public AppRunner1() {
		super(AppRunner1.class, new DuplicationDetails("a", 123, 1.23), new DuplicationDetails("b", 234, 2.34), new DuplicationDetails("c", 345, 3.45));
	}

	@Inject
	void verifyDynamicDuplicateInjection(@InjectAll List<DynamicDuplicate> all, @Named("a") DynamicDuplicate a, @Named("b") DynamicDuplicate b, @Named("c") DynamicDuplicate c) {
		timesVerifyDynamicDuplicatesCalled++;
	}

	@Override
	public void run() {
		super.run();
		assertion(timesVerifyDynamicDuplicatesCalled == 1, "verifyDynamicDuplicateInjection() was expected to be called once, but was called " + timesVerifyDynamicDuplicatesCalled + " times");
		assertDynamicDuplicatesInAll(Map.of("A", aDup, "B", bDup, "C", cDup));
		System.out.println("Dynamic duplicates validated!");
	}
}
