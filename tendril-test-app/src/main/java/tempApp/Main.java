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

import tendril.bean.Inject;
import tendril.bean.qualifier.Named;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;

/**
 * 
 */
@Runner
public class Main implements TendrilRunner {
    
    @Inject
    @Named("TempName")
    Object tmpClass;

    @Inject
    FactoryClass factoryBean1;
    @Inject
    FactoryClass factoryBean2;
    @Inject
    FactoryClass factoryBean3;
    @Inject
    FactoryClass factoryBean4;

    @Override
    public void run() {
        System.out.println("RUNNING!!! " + tmpClass);
        System.out.println(factoryBean1);
        System.out.println(factoryBean2);
        System.out.println(factoryBean3);
        System.out.println(factoryBean4);
        assert(factoryBean1 != factoryBean2);
        assert(factoryBean1 != factoryBean3);
        assert(factoryBean1 != factoryBean4);
        assert(factoryBean2 != factoryBean3);
        assert(factoryBean2 != factoryBean4);
        assert(factoryBean3 != factoryBean4);
    }
}
