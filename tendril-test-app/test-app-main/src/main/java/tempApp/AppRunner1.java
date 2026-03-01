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

import tempApp.duplicate.DynamicDuplicate;
import tendril.bean.Inject;
import tendril.bean.qualifier.Named;
import tendril.bean.requirement.RequiresEnv;
import tendril.context.launch.Runner;

@Runner
@RequiresEnv("AppRunner1")
public class AppRunner1 extends AbstractAppRunner {

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
    
    @Override
    public void run() {
    	super.run();

    	assertion(dynamicDuplicates.contains(aDup), "Duplicate A is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(bDup), "Duplicate B is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(cDup), "Duplicate C is not present in dynamicDuplicates");
    }
}
