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
    private FactoryClass factoryBean5;
    
    @Inject
    void doSomething(SingletonClass singleton1, SingletonClass singleton2, FactoryClass factory1, FactoryClass factory2, FactoryClass factory3) {
        System.out.println("doSomething()");
        factoryBean5 = factory1;

        System.out.println(factory1);
        System.out.println(factory2);
        System.out.println(factory3);
        
        assert(singleton1 == singleton2);
        assert(factory1 != factory2);
        assert(factory1 != factory3);
        assert(factory2 != factory3);
    }
    
    @Inject
    void doSomethingElse(FactoryClass factory1) {
        System.out.println("doSomethingElse()");
        System.out.println(factory1);
    }
    
    @Inject
    void doNothing() {
        System.out.println("doNothing()");
    }

    @Override
    public void run() {
        System.out.println("RUNNING!!! " + tmpClass);
        System.out.println(factoryBean1);
        System.out.println(factoryBean2);
        System.out.println(factoryBean3);
        System.out.println(factoryBean4);
        System.out.println(factoryBean5);
        assert(factoryBean1 != factoryBean2);
        assert(factoryBean1 != factoryBean3);
        assert(factoryBean1 != factoryBean4);
        assert(factoryBean1 != factoryBean5);
        assert(factoryBean2 != factoryBean3);
        assert(factoryBean2 != factoryBean4);
        assert(factoryBean2 != factoryBean5);
        assert(factoryBean3 != factoryBean4);
        assert(factoryBean3 != factoryBean5);
        assert(factoryBean4 != factoryBean5);
    }
}
