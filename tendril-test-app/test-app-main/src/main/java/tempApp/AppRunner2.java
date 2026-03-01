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
import tendril.bean.requirement.RequiresNotEnv;
import tendril.context.launch.Runner;

@Runner
@RequiresNotEnv("AppRunner1")
public class AppRunner2 extends AbstractAppRunner {
	
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
        super(AppRunner2.class, new DuplicationDetails("d", 321, 3.21), new DuplicationDetails("e", 432, 4.32), new DuplicationDetails("f", 543, 5.43),
        		new DuplicationDetails("g", 654, 6.54), new DuplicationDetails("h", 765, 7.65), new DuplicationDetails("i", 876, 8.76));
    }
    
    @Override
    public void run() {
    	super.run();

    	assertion(dynamicDuplicates.contains(dDup), "Duplicate D is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(eDup), "Duplicate E is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(fDup), "Duplicate F is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(gDup), "Duplicate G is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(hDup), "Duplicate H is not present in dynamicDuplicates");
    	assertion(dynamicDuplicates.contains(iDup), "Duplicate I is not present in dynamicDuplicates");
    }
}
